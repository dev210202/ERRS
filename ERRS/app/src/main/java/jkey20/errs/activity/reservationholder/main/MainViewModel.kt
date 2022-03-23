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
    private val _waitingTeamsNumber = MutableStateFlow("")
    val waitingTeamsNumber = _waitingTeamsNumber.asStateFlow()

    private val _myWaitingNumber = MutableStateFlow(0)
    val myWaitingNumber = _myWaitingNumber.asStateFlow()

    fun setRestaurantName(restaurantName: String) = viewModelScope.launch {
        _restaurantName.emit(restaurantName)
    }

    fun getRestaurantName(): String {
        return _restaurantName.value
    }

    fun setDeviceUUID(uuid: String) = viewModelScope.launch {
        Log.i("UUID", uuid)
        _deviceUUID.emit(uuid)
    }

    fun getDeviceUUID(): String {
        return _deviceUUID.value
    }

    //    fun setReservationNumber(reservationNumber: String) = viewModelScope.launch {
//        _reservationNumber.emit(reservationNumber)
//    }
//
//    fun getReservationNumber(): String {
//        return _reservationNumber.value
//    }
//
//    fun setWaitingTeamsNumber(waitingTeamsNumber: Int) = viewModelScope.launch {
//        _waitingTeamsNumber.emit(waitingTeamsNumber)
//    }
//
//    fun getWaitingTeamsNumber(): String {
//        return _waitingTeamsNumber.value.toString()
//    }
//
//    fun setMyWaitingTeamsNumber(myWaitingNumber: Int) = viewModelScope.launch {
//        _myWaitingNumber.emit(myWaitingNumber)
//    }
//
    fun getMyWaitingTeamsNumber(): String {
        return _myWaitingNumber.value.toString()
    }


    fun addReservation(restaurantName: String, reservation: Reservation) = viewModelScope.launch {
        runCatching {
            repository.createReservation(restaurantName, reservation, Math.random().toString())
        }.onSuccess { isSuccess ->
            Log.i("addReservation", isSuccess.toString())
            if (isSuccess) {

            }
        }.onFailure { error ->
            Log.e("error", error.message.toString())
        }

    }

    fun readReservation(restaurantName: String) = viewModelScope.launch {
        runCatching {
            repository.readReservation(restaurantName)
        }.onSuccess { reservationList ->
            _myWaitingNumber.emit(reservationList.size)
            // _waitingTeamsNumber.emit(reservationList.size)
            if (reservationList.size > 0) {
                _reservationNumber.emit((reservationList.size + 1).toString())
                Log.i("readReservation1", "success")
            } else {
                _reservationNumber.emit("1")
                Log.i("readReservation2", "success")
            }
        }.onFailure { error ->
            Log.e("error", error.message.toString())
        }
    }

    fun addRealtimeWaitingTeamsUpdate(restaurantName: String) = viewModelScope.launch {
        repository.readRealtimeWaitingTeamsUpdate(restaurantName).collect { waitingTeamsNumber ->
            Log.e("waitingTeamsNumber: ", waitingTeamsNumber)
            _waitingTeamsNumber.emit(waitingTeamsNumber)
        }

    }

    fun perceiveESL(): String {
        // TODO: perceive ESL
        val restaurantName = ""
        return restaurantName
    }


}