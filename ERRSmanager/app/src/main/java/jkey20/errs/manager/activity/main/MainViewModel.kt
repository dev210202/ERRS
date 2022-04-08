package jkey20.errs.manager.activity.main

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.DocumentChange
import dagger.hilt.android.lifecycle.HiltViewModel
import jkey20.errs.base.BaseViewModel
import jkey20.errs.manager.repository.FirebaseRepository
import jkey20.errs.manager.util.toObjectNonNull
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

    // 현재 대기 팀 수
    private val _waitingTeamsNumber = MutableStateFlow("0")
    val waitingTeamsNumber = _waitingTeamsNumber.asStateFlow()

    fun setRestaurantName(restaurantName: String) = viewModelScope.launch {
        _restaurantName.emit(restaurantName)
    }

    fun loadRestaurantName(): String {
        return _restaurantName.value
    }

    fun loadReservationList(): List<Reservation> {
        return _reservationList.value
    }


    /* 1. 실시간 대기팀 업데이트
       2. 예약 변경시 해당 에약 업데이트
     */
    fun getRealtimeChanges(restaurantName: String) = viewModelScope.launch {
        repository.readRealtimeUpdate(restaurantName).collect { value ->
            // 1
            _waitingTeamsNumber.emit(value.size().toString())
            Log.e("VALUE SIZE", value.size().toString())
            for (dc in value!!.documentChanges) {
                var list = _reservationList.value.toMutableList()
                when (dc.type) {
                    DocumentChange.Type.ADDED -> {
                        Log.e("DC ADDED", dc.document.data.toString())
                        val reservation: Reservation = dc.document.toObjectNonNull()
                        if (!list.contains(reservation)) {
                            list.add(dc.document.toObjectNonNull())
                        }
                    }
                    DocumentChange.Type.MODIFIED -> {
                        Log.e("DC MODIFIED", dc.document.data.toString())
                        val modifyReservation = dc.document.toObjectNonNull<Reservation>()
                        var originReservation = Reservation()
                        list.forEach { reservation ->
                            if (reservation.reservationNumber.equals(modifyReservation.reservationNumber)) {
                                originReservation = reservation
                            }
                        }
                        list.set(list.indexOf(originReservation), modifyReservation)
//                        list.remove(originReservation)
//                        list.add(modifyReservation)
                    }
                    DocumentChange.Type.REMOVED -> {
                        Log.e("DC REMOVED", dc.document.data.toString())
                        list.remove(dc.document.toObjectNonNull())
                    }
                }
                list.sortBy { it.reservationNumber }
                _reservationList.emit(list)
            }
        }
    }


    fun getReservationList(restaurantName: String) = viewModelScope.launch {
        runCatching {
            repository.readReservationList(restaurantName)
        }.onSuccess { value ->
            val list = mutableListOf<Reservation>()
            value.documents.forEach { documentSnapshot ->
                list.add(documentSnapshot.toObjectNonNull())
            }
            list.sortBy { it.reservationNumber }
            _reservationList.emit(list)
        }.onFailure { error ->
            Log.e("error", error.message.toString())
        }
    }

    fun cancelReservation(restaurantName: String, reservation: Reservation) =
        viewModelScope.launch {
            runCatching {
                repository.deleteReservation(restaurantName, reservation.uuid)
            }.onSuccess { isSuccess ->
                Log.i("예약취소 성공", isSuccess.toString())
                if (isSuccess) {

                }

            }.onFailure { error ->
                Log.e("예약취소 에러", error.message.toString())
            }
        }

    fun removeRealTimeUpdate() {
        repository.removeRegistrationList()
    }

}