package jkey20.errs.repository

import android.util.Log
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


    suspend fun readRealtimeWaitingTeamsUpdate(restaurantName: String) = callbackFlow {
        try{
            db.collection(restaurantName).addSnapshotListener { value, error ->
                if (error != null) {
                    throw error
                }
                trySend(value!!.size().toString())
            }
        }catch (exception: Exception){
            trySend(exception.toString())
        }
        awaitClose()
    }

}