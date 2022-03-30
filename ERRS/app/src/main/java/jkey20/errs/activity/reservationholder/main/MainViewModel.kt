package jkey20.errs.activity.reservationholder.main

import android.util.Log
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jkey20.errs.base.BaseViewModel
import jkey20.errs.model.firebase.Menu
import jkey20.errs.model.firebase.Order
import jkey20.errs.model.firebase.Reservation
import jkey20.errs.repository.FirebaseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val repository: FirebaseRepository) :
    BaseViewModel() {

    private val _restaurantName = MutableStateFlow(String())
    val restaurantName = _restaurantName.asStateFlow()

    private val _deviceUUID = MutableStateFlow(String())
    val deviceUUID = _deviceUUID.asStateFlow()

    private val _reservation = MutableStateFlow(Reservation())
    val reservation = _reservation.asStateFlow()

    private val _reservationNumber = MutableStateFlow(String())
    val reservationNumber = _reservationNumber.asStateFlow()

    // 현재 대기 팀 수
    private val _waitingTeamsNumber = MutableStateFlow("0")
    val waitingTeamsNumber = _waitingTeamsNumber.asStateFlow()

    private val _myWaitingNumber = MutableStateFlow("0")
    val myWaitingNumber = _myWaitingNumber.asStateFlow()

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

    fun loadMyWaitingTeamsNumber(): String {
        return _myWaitingNumber.value
    }

    // TODO 4: 예약 추가
    fun addReservation(restaurantName: String, reservation: Reservation) = viewModelScope.launch {
        runCatching {
            repository.createReservation(
                restaurantName,
                reservation,
                getDeviceUUID()
            ) // TODO: random -> UUID로 변경하기
        }.onSuccess { isSuccess ->
            Log.i("예약추가 성공", isSuccess.toString())
            if (isSuccess) {
                _reservation.emit(reservation)
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

    // TODO 2: 예약번호 지정
    fun setReservationNumber(restaurantName: String) = viewModelScope.launch {
        runCatching {
            repository.readReservation(restaurantName)
        }.onSuccess { reservationList ->

            // TODO 2-1: 최대한 큰 예약번호의 다음번호로 예약번호 지정
            if (reservationList.size > 0) {
                var topNumber = "0"
                reservationList.forEach { reservation ->
                    if (reservation.reservationNumber.toInt() >= topNumber.toInt()) {
                        topNumber = reservation.reservationNumber
                    }
                }
                _reservationNumber.emit((topNumber.toInt() + 1).toString())
            } else {
                _reservationNumber.emit("1")
            }
        }.onFailure { error ->
            Log.e("error", error.message.toString())
        }
    }


    // TODO 6: 대기순번 업데이트
    fun addRealtimeMyWaitingNumber(restaurantName: String) = viewModelScope.launch {
        repository.readRealtimeMyWaitingNumber(
            restaurantName,
            loadReservationNumber(),
            loadMyWaitingTeamsNumber()
        ).collect { myWaitingNumber ->
            Log.e("나의 대기순번: ", myWaitingNumber)
            _myWaitingNumber.emit(myWaitingNumber)

        }
    }

    // TODO 5: 대기팀 수  업데이트
    fun addRealtimeWaitingTeamsUpdate(restaurantName: String) = viewModelScope.launch {
        repository.readRealtimeWaitingTeamsUpdate(restaurantName).collect { waitingTeamsNumber ->
            Log.e("대기팀수 업데이트: ", waitingTeamsNumber)
            _waitingTeamsNumber.emit(waitingTeamsNumber)
        }
    }

    // TODO: 내 주문 업데이트
    fun addRealtimeMyOrder(){

    }

    fun cancelOrderMenu(restaurantName: String) = viewModelScope.launch {
        runCatching {
            repository.deleteOrderMenu(restaurantName, getDeviceUUID(), loadReservationOrder())
        }.onSuccess { isSuccess ->
            Log.i("메뉴주문취소 성공", isSuccess.toString())
        }.onFailure { error ->
            Log.e("메뉴주문 취소 error", error.message.toString())
        }
    }

    fun loadReservationOrder() : Order{
        return _reservation.value.order
    }

    fun editReservationOrder(deleteMenu: Menu){
        _reservation.value.order.menuList.forEach { menu ->
            Log.i("menu",menu.name)
            if(menu.equals(deleteMenu)){
                val list = _reservation.value.order.menuList.toMutableList()
                list.remove(menu)
                _reservation.value.order.menuList = list
                Log.e("SIZE", ""+_reservation.value.order.menuList.size)
            }
        }
    }

    fun perceiveESL(): String {
        // TODO: perceive ESL
        val restaurantName = ""
        return restaurantName
    }

}