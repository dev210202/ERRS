package jkey20.errs.repository

import android.util.Log
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import jkey20.errs.model.firebase.Reservation
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.*
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import java.lang.Exception
import kotlin.coroutines.resume

class FirebaseRepository {

    val db = Firebase.firestore

    @ExperimentalCoroutinesApi
    suspend fun createReservation(
        restaurantName: String,
        reservation: Reservation,
        uuid: String
    ): Boolean =
        suspendCancellableCoroutine { continuation ->
            db.collection(restaurantName). // 식당이름 컬렉션
            document(uuid). // uuid 문서
            set(reservation) // 예약정보 필드
                .addOnSuccessListener {
                    continuation.resume(true)
                }
                .addOnFailureListener { exception ->
                    continuation.resume(false)
                    throw exception
                }
        }

    suspend fun readReservation(restaurantName: String): List<Reservation> =
        suspendCancellableCoroutine { continuation ->
            db.collection(restaurantName).get()
                .addOnSuccessListener { result ->
                    continuation.resume(result.documents.map(DocumentSnapshot::toObjectNonNull))
                }
                .addOnFailureListener { exception ->
                    continuation.resume(emptyList())
                    throw exception
                }
        }

    suspend fun deleteReservation(restaurantName: String, uuid: String): Boolean =
        suspendCancellableCoroutine { continuation ->
            db.collection(restaurantName).document(uuid).delete().addOnSuccessListener {
                continuation.resume(true)
            }.addOnFailureListener { exception ->
                continuation.resume(false)
                throw  exception
            }
        }

    /*
     처음 대기순번이 없을때
     - 예약번호를 기준으로 나보다 앞에 몇명이 있는지 체크

      대기순번 존재 이후
      - 나보다 앞의 번호가 취소하면 대기순번 감소!
     */
    suspend fun readRealtimeMyWaitingNumber(
        restaurantName: String,
        reservationNumber: String,
        myWaitingNumber: String
    ) = callbackFlow {
        try {
            db.collection(restaurantName).addSnapshotListener { value, error ->
                if (error != null) {
                    throw error
                }
                // 처음 대기순번이 없을 때
                if (myWaitingNumber == "0") {
                    var waitingNumber = 0
                    for (doc in value!!.documents) {
                        if (doc.get("reservationNumber").toString()
                                .toInt() < reservationNumber.toInt()
                        ) {
                            waitingNumber++
                        }
                    }
                    trySend(waitingNumber.toString())
                } else // 대기순번 존재
                    for (dc in value!!.documentChanges) {
                        when (dc.type) {
                            DocumentChange.Type.REMOVED -> {
                                Log.e("DC REMOVED", dc.document.data.toString())
                                val removeData: String =
                                    dc.document.data.get("reservationNumber").toString()
                                // 지워진 데이터의 예약번호가 나보다 앞일떄(작을때)
                                if (removeData.toInt() < reservationNumber.toInt()) {
                                    trySend((myWaitingNumber.toInt() - 1).toString())
                                }
                            }
                        }
                    }
            }
        } catch (exception: Exception) {
            trySend(exception.toString())
        }
        awaitClose()
    }

    suspend fun readRealtimeWaitingTeamsUpdate(restaurantName: String) = callbackFlow {
        try {
            db.collection(restaurantName).addSnapshotListener { value, error ->
                if (value != null) {
                    trySend(value.size().toString())
                }
                if (error != null) {
                    throw error
                }
            }
        } catch (exception: Exception) {
            trySend(exception.toString())
        }
        awaitClose()
    }

}