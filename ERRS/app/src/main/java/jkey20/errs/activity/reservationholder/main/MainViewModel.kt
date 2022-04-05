package jkey20.errs.activity.reservationholder.main

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import dagger.hilt.android.lifecycle.HiltViewModel
import jkey20.errs.base.BaseViewModel
import jkey20.errs.model.firebase.Menu
import jkey20.errs.model.firebase.Order
import jkey20.errs.model.firebase.Reservation
import jkey20.errs.repository.FirebaseRepository
import jkey20.errs.repository.toObjectNonNull
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val repository: FirebaseRepository) :
    BaseViewModel() {

    private val _restaurantName = MutableStateFlow(String())
    val restaurantName = _restaurantName.asStateFlow()

    private val _deviceUUID = MutableStateFlow(String())
    val deviceUUID = _deviceUUID.asStateFlow()

    private val _isReserved = MutableStateFlow(String())
    val isReserved = _isReserved.asStateFlow()

    private val _reservationList = MutableStateFlow(listOf<Reservation>())
    val reservationList = _reservationList.asStateFlow()

    private val _reservation = MutableStateFlow(Reservation())
    val reservation = _reservation.asStateFlow()

    private val _reservationNumber = MutableStateFlow(String())
    val reservationNumber = _reservationNumber.asStateFlow()

    // 현재 대기 팀 수
    private val _waitingTeamsNumber = MutableStateFlow("0")
    val waitingTeamsNumber = _waitingTeamsNumber.asStateFlow()

    private val _myWaitingNumber = MutableStateFlow("-1")
    val myWaitingNumber = _myWaitingNumber.asStateFlow()


    fun getIsReserved(): String {
        return _isReserved.value
    }

    fun setRestaurantName(restaurantName: String) = viewModelScope.launch {
        _restaurantName.emit(restaurantName)
    }

    fun loadRestaurantName(): String {
        return _restaurantName.value
    }

    fun loadReservation(): Reservation {
        return _reservation.value
    }

    fun setDeviceUUID(uuid: String) = viewModelScope.launch {
        Log.i("UUID", uuid)
        _deviceUUID.emit(uuid)
    }

    fun getDeviceUUID(): String {
        return _deviceUUID.value
    }

    fun loadReservationNumber(): String {
        return _reservationNumber.value
    }

    fun loadMyWaitingNumber(): String {
        return _myWaitingNumber.value
    }

    fun checkMyReservation(restaurantName: String) = viewModelScope.launch {
        runCatching {
            repository.readReservationList(restaurantName)
        }.onSuccess { value ->

            // 내 예약 존재하는지 확인
            var isExistReservation = false
            value.documents.forEach { documentSnapshot ->
                if (documentSnapshot.id.equals(getDeviceUUID())) {
                    isExistReservation = true
                }
            }

            // 전체 리스트 조회
            val list = mutableListOf<Reservation>()
            value.documents.forEach { documentSnapshot ->
                if (documentSnapshot.id.equals(getDeviceUUID())) {
                    _reservation.emit(documentSnapshot.toObjectNonNull())
                }
                list.add(documentSnapshot.toObjectNonNull())
            }
            _reservationList.emit(list)
            _isReserved.emit(isExistReservation.toString())
        }
    }

    fun addMyWaitingNumber() = viewModelScope.launch {
        val list = _reservationList.value.toMutableList()
        list.sortBy { it.reservationNumber }
        val waitingNumber = list.indexOf(_reservation.value)
        _myWaitingNumber.emit(waitingNumber.toString())
    }

    fun createReservation() = viewModelScope.launch {
        _reservation.emit(
            Reservation(
                reservationNumber = createNewReservationNumber(),
                time = getCurrentTime(),
                order = Order(
                    menuList = mutableListOf(
                        Menu(
                            name = "삼각김밥",
                            status = "접수완료"
                        ), Menu(
                            name = "사각김밥",
                            status = "접수미완"
                        )
                    )
                )
            )
        )
    }

    fun addReservation(restaurantName: String, reservation: Reservation) = viewModelScope.launch {
        runCatching {
            repository.createReservation(
                restaurantName,
                reservation,
                getDeviceUUID()
            )
        }.onSuccess { isSuccess ->
            Log.i("예약추가 성공", isSuccess.toString())
            if (isSuccess) {
                val list = _reservationList.value.toMutableList()
                list.add(reservation)
               _reservationList.emit(list)
                addMyWaitingNumber()
            }
        }.onFailure { error ->
            Log.e("error", error.message.toString())
        }

    }

    fun cancelReservation(restaurantName: String) = viewModelScope.launch {
        runCatching {
            repository.deleteReservation(restaurantName, getDeviceUUID())
        }.onSuccess { isSuccess ->
            Log.i("예약취소 성공", isSuccess.toString())
            if (isSuccess) {

            }

        }.onFailure { error ->
            Log.e("예약취소 에러", error.message.toString())
        }
    }


    fun cancelOrderMenu(restaurantName: String, order: Order) = viewModelScope.launch {
        runCatching {
            repository.deleteOrderMenu(restaurantName, getDeviceUUID(), order)
        }.onSuccess { isSuccess ->
            Log.i("메뉴주문취소 성공", isSuccess.toString())
            val reservation = Reservation(
                reservationNumber = _reservation.value.reservationNumber,
                time = _reservation.value.time,
                order = order
            )
            _reservation.emit(reservation)
        }.onFailure { error ->
            Log.e("메뉴주문 취소 error", error.message.toString())
        }
    }


    /*
        기능
        1. 실시간 대기팀 업데이트
        2. 예약에 변경이 생기면 대기순번, 주문 업데이트

     */
    fun addRealtimeUpdate(restaurantName: String) = viewModelScope.launch {
        repository.readRealtimeUpdate(restaurantName).collect { value ->
            // 1
            _waitingTeamsNumber.emit(value.size().toString())
            // 2
            for (dc in value!!.documentChanges) {
                Log.e("DC ID: ", dc.document.id)

                when (dc.type) {

                    DocumentChange.Type.REMOVED -> {
                        Log.e("DC REMOVED", dc.document.data.toString())
                        val list = mutableListOf<Reservation>()
                        value.documents.forEach { documentSnapshot ->
                            val reservation: Reservation = documentSnapshot.toObjectNonNull()
                            list.add(reservation)
                        }
                        list.sortBy { it.reservationNumber }
                        _reservationList.emit(list)
                        addMyWaitingNumber()
                    }

                    // 메뉴변경시
                    DocumentChange.Type.MODIFIED -> {
                        if (dc.document.id.equals(getDeviceUUID())) {
                            Log.e("DC MODIFIED1", dc.document.data.toString())
                            _reservation.emit(dc.document.toObjectNonNull())
                        }
                        Log.e("DC MODIFIED2", dc.document.data.toString())
                        val list = mutableListOf<Reservation>()
                        value.documents.forEach { documentSnapshot ->
                            val reservation: Reservation = documentSnapshot.toObjectNonNull()
                            list.add(reservation)
                        }
                        list.sortBy { it.reservationNumber }
                        _reservationList.emit(list)
                        addMyWaitingNumber()
                    }
                }
            }
        }
    }

    fun removeRealTimeUpdate() {
        repository.removeRegistrationList()
    }

    fun editReservationOrder(deleteMenu: Menu): Order {
        val order = Order(
            menuList = _reservation.value.order.menuList,
            request = _reservation.value.order.request
        )
        _reservation.value.order.menuList.forEach { menu ->
            Log.i("menu", menu.name)
            if (menu.equals(deleteMenu)) {
                val list = order.menuList.toMutableList()
                list.remove(menu)
                Log.e("SIZE", "" + _reservation.value.order.menuList.size)
                order.menuList = list
            }
        }
        return order
    }


    fun createNewReservationNumber(): String {
        if (_reservationList.value.size > 0) {
            var topNumber = "0"
            _reservationList.value.forEach { reservation ->
                Log.e("CREATE RN RESERVATION", reservation.reservationNumber)
                if (reservation.reservationNumber.toInt() >= topNumber.toInt()
                ) {
                    topNumber = reservation.reservationNumber
                }
            }
            return (topNumber.toInt() + 1).toString()
        } else {
            return "1"
        }
    }


    private fun getCurrentTime(): String {
        val now = System.currentTimeMillis()
        val date = Date(now)
        val format = SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
        return format.format(date)
    }

    fun perceiveESL(): String {
        // TODO: perceive ESL
        val restaurantName = ""
        return restaurantName
    }

}