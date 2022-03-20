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

    private val _reservation = MutableStateFlow(Reservation())
    val reservation = _reservation.asStateFlow()

    fun addReservation(reservation: Reservation) = viewModelScope.launch {
        runCatching {
            repository.createReservation("restaurant310", reservation)
        }.onSuccess { isSuccess ->
            Log.i("asd", isSuccess.toString())
            if (isSuccess) {

            }
        }.onFailure { error ->
            Log.e("error", error.message.toString())

        }

    }

}