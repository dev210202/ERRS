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
import kotlinx.coroutines.flow.update
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
                when (dc.type) {
                    DocumentChange.Type.ADDED -> {
                        //
                        Log.e("DC ADDED", dc.document.data.toString())
                        val list = _reservationList.value.toMutableList()
                        list.add(dc.document.toObjectNonNull())
                        list.sortBy { it.reservationNumber }
                        _reservationList.emit(list)
                    }
                    DocumentChange.Type.MODIFIED -> {
                        // 수정
                        // 리스트에서 수정된 예약을 찾아서 수정
                        Log.e("DC MODIFIED", dc.document.data.toString())
                        val list = _reservationList.value.toMutableList()
                        // Todo : 리스트에서 예약 수정 
                    }
                    DocumentChange.Type.REMOVED -> {
                        // 삭제 -> 예약 자체가 삭제
                        // 리스트에서 삭제된 예약을 찾아서 제거
                        Log.e("DC REMOVED", dc.document.data.toString())
                        val list = _reservationList.value.toMutableList()
                        list.remove(dc.document.toObjectNonNull())
                        list.sortBy { it.reservationNumber }
                        _reservationList.emit(list)
                    }
                }
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
}