package jkey20.errs.repository

import android.util.Log
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import jkey20.errs.model.firebase.Order
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

    suspend fun readReservation(restaurantName: String, uuid : String): Boolean =
        suspendCancellableCoroutine { continuation ->
            db.collection(restaurantName).get()
                .addOnSuccessListener { result ->
                    var isExistReservation = false
                    result.documents.forEach { documentSnapshot ->
                        if(documentSnapshot.id.equals(uuid)){
                           isExistReservation = true
                        }
                    }
                    continuation.resume(isExistReservation)
                }
                .addOnFailureListener { exception ->
                    continuation.resume(true)
                    throw exception
                }
        }

    suspend fun readReservationList(restaurantName: String): QuerySnapshot=
        suspendCancellableCoroutine { continuation ->
            db.collection(restaurantName).get()
                .addOnSuccessListener { result ->
                    continuation.resume(result)
                }
                .addOnFailureListener { exception ->
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


    suspend fun readRealtimeUpdate(restaurantName: String) = callbackFlow {
        db.collection(restaurantName).addSnapshotListener { value, error ->
            if (value != null) {
                Log.e("READ REAL TIME UPDATE", "!")
                trySend(value)
            }
            if (error != null) {
                throw error
            }
        }
        awaitClose()
    }

    suspend fun deleteOrderMenu(restaurantName: String, uuid: String, order : Order): Boolean =
        suspendCancellableCoroutine { continuation ->
            db.collection(restaurantName).document(uuid)
                .update("order", order).addOnSuccessListener {
                    continuation.resume(true)
                }.addOnFailureListener { exception ->
                    continuation.resume(false)
                    throw  exception
                }
        }

}