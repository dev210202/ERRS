package jkey20.errs.activity.reservationholder.main

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import dagger.hilt.android.lifecycle.HiltViewModel
import jkey20.errs.base.BaseViewModel
import jkey20.errs.model.cart.CartMenu
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

    private val _deviceToken = MutableStateFlow(String())
    val deviceToken = _deviceToken.asStateFlow()

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

    private val _myWaitingNumber = MutableStateFlow(" ")
    val myWaitingNumber = _myWaitingNumber.asStateFlow()


    fun getIsReserved(): String {
        return _isReserved.value
    }

    fun setRestaurantName(restaurantName: String) = viewModelScope.launch {
        _restaurantName.emit(restaurantName)
    }

    fun loadRestaurantName(): String {
        return _restaurantName.value
        // get~으로 함수명을 지으면 데이터바인딩에서 에러가 나서 get이 아닌 load로 지정
    }

    fun loadReservation(): Reservation {
        return _reservation.value
    }

    fun setToken(token: String) = viewModelScope.launch {
        _deviceToken.emit(token)
    }

    fun getDeviceToken(): String {
        return _deviceToken.value
    }

    fun loadReservationNumber(): String {
        return _reservationNumber.value
    }

    fun loadMyWaitingNumber(): String {
        return _myWaitingNumber.value
    }

    fun checkMyReservation(restaurantName: String) = viewModelScope.launch {
        runCatching {

            Log.e("checkMyReservation 시작", "레스토랑이름: " + restaurantName)
            repository.readReservationList(restaurantName)
        }.onSuccess { value ->
            Log.e("checkMyReservation 완료", "!")
            // 내 예약 존재하는지 확인
            var isExistReservation = false
            value.documents.forEach { documentSnapshot ->
                Log.e("checkMyReservation 예약존재 확인", "!")
                if (documentSnapshot.id.equals(getDeviceToken())) {
                    isExistReservation = true
                }
            }
            Log.e("예약여부 :", isExistReservation.toString())
            _isReserved.emit(isExistReservation.toString())



            // 전체 리스트 조회
            val list = mutableListOf<Reservation>()
            value.documents.forEach { documentSnapshot ->
                Log.e("checkMyReservation 전체리스트 확인", "!")
                if (documentSnapshot.id.equals(getDeviceToken())) {
                    _reservation.emit(documentSnapshot.toObjectNonNull())
                }
                list.add(documentSnapshot.toObjectNonNull())
            }
            Log.e("list", list.toString())
            _reservationList.emit(list)

        }
    }

    fun addMyWaitingNumber() = viewModelScope.launch {
        Log.e("addMyWaitingNumber 시작", "!")
        val list = _reservationList.value.toMutableList()
        list.sortBy { it.reservationNumber }
        val waitingNumber = list.indexOf(_reservation.value)
        Log.e("addMyWaitingNumber 완료", "!")
        _myWaitingNumber.emit(waitingNumber.toString())
    }

    fun createReservation() = viewModelScope.launch {
        Log.e("createReservation", "!")
        _reservation.emit(
            Reservation(
                reservationNumber = createNewReservationNumber(),
                time = getCurrentTime(),
                order = Order(
                    menuList = mutableListOf(
                        CartMenu(
                            Menu(
                                name = "삼각김밥",
                                status = "접수완료"
                            )
                        ),
                        CartMenu(
                            Menu(
                                name = "사각김밥",
                                status = "접수미완"
                            )
                        )
                    )
                ),
                token = getDeviceToken()
            )
        )
    }

    fun addReservation(restaurantName: String, reservation: Reservation) = viewModelScope.launch {
        runCatching {
            Log.e("addReservation 시작", "!")
            repository.createReservation(
                restaurantName,
                reservation,
                getDeviceToken()
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
            Log.e("cancelReservation 시작", "!")
            repository.deleteReservation(restaurantName, getDeviceToken())
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
            repository.updateOrderMenu(restaurantName, getDeviceToken(), order)
        }.onSuccess { isSuccess ->
            Log.i("메뉴주문취소 성공", isSuccess.toString())
            val reservation = Reservation(
                reservationNumber = _reservation.value.reservationNumber,
                time = _reservation.value.time,
                order = order,
                token = _reservation.value.token
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
        Log.e("addRealtimeUpdate 시작", "!")
        repository.readRealtimeUpdate(restaurantName).collect { value ->
            // 1
            _waitingTeamsNumber.emit(value.size().toString())
            // 2
            for (dc in value!!.documentChanges) {

                when (dc.type) {

                    DocumentChange.Type.REMOVED -> {
                        Log.e("제거", dc.document.data.toString())
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
                        if (dc.document.id.equals(getDeviceToken())) {
                            Log.e("수정", dc.document.data.toString())
                            _reservation.emit(dc.document.toObjectNonNull())
                        }
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

    fun editReservationOrder(deleteMenu: CartMenu): Order {
        val order = _reservation.value.order.copy()

        _reservation.value.order.menuList.forEach { cartMenu ->
            Log.i("menu", cartMenu.menu.name)
            if (cartMenu.equals(deleteMenu)) {
                val list = order.menuList.toMutableList()
                list.remove(cartMenu)
                Log.e("SIZE", "" + _reservation.value.order.menuList.size)
                order.menuList = list
            }
        }
        return order
    }


    fun createNewReservationNumber(): String {
        Log.e("createNewReservationNumber 시작", "!")
        if (_reservationList.value.size > 0) {
            var topNumber = "0"
            _reservationList.value.forEach { reservation ->
                Log.e("예약 번호: ", reservation.reservationNumber)
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