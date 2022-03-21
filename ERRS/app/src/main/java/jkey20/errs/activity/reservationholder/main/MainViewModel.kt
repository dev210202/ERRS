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
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val repository: FirebaseRepository) :
    BaseViewModel() {

    private val _restaurantName = MutableStateFlow(String())
    val restaurantName = _restaurantName.asStateFlow()

    val userUUID = ""


    private val _reservation = MutableStateFlow(Reservation())
    val reservation = _reservation.asStateFlow()
    private val _reservationNumber = MutableStateFlow(String())
    val reservationNumber = _reservationNumber.asStateFlow()

    private val _waitingTeamsNumber = MutableStateFlow(0)
    val waitingTeamsNumber = _waitingTeamsNumber.asStateFlow()

    fun setRestaurantName() = viewModelScope.launch {
        _restaurantName.emit("320")
    }

    fun getRestaurantName() : String {
        return _restaurantName.value
    }

    fun addReservation(restaurantName: String, reservation: Reservation) = viewModelScope.launch {
        runCatching {
            repository.createReservation(restaurantName, reservation)
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
            _waitingTeamsNumber.emit(reservationList.size)
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

    fun perceiveESL(): String {
        // TODO: perceive ESL
        val restaurantName = ""
        return restaurantName
    }

    fun getDeviceUUID(): String {
        // TODO: get Device UUID
        val deviceUUID = ""
        return deviceUUID
    }
}