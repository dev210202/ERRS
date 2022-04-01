package jkey20.errs.manager.activity.main

import android.util.Log
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jkey20.errs.base.BaseViewModel
import jkey20.errs.manager.repository.FirebaseRepository
import jkey20.errs.model.firebase.Reservation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val repository: FirebaseRepository) :
    BaseViewModel() {

    private val _restaurantName = MutableStateFlow(String())
    val restaurantName = _restaurantName.asStateFlow()

    private val _reservationList = MutableStateFlow(listOf<Reservation>())
    val reservationList = _reservationList.asStateFlow()

    fun loadReservationList(): List<Reservation> {
        return _reservationList.value
    }

    fun getRealtimeChanges(restaurantName: String) = viewModelScope.launch {

        repository.readRealtimeChanges(restaurantName, loadReservationList())
            .collect { reservationList ->
                _reservationList.emit(reservationList)
            }

    }

    fun getReservationList(restaurantName: String) = viewModelScope.launch {
        runCatching {
            repository.readReservation(restaurantName)
        }.onSuccess { reservationList ->
            _reservationList.emit(reservationList)
            Log.i("SUCCESS", "GET RESERVATIONLIST")
            reservationList.forEach { reservation ->
                // reservation 번호순으로 정렬해야함
                Log.e("RESERVATIOn", reservation.reservationNumber)
            }
        }.onFailure { error ->
            Log.e("error", error.message.toString())
        }
    }

}