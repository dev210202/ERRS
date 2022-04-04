package jkey20.errs.manager.repository

import android.util.Log
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import jkey20.errs.manager.model.firebase.RealTimeData
import jkey20.errs.manager.util.toObjectNonNull
import jkey20.errs.model.firebase.Reservation
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class FirebaseRepository {

    val db = Firebase.firestore


    suspend fun readRealtimeUpdate(restaurantName: String) =
        callbackFlow {
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

    suspend fun readReservationList(restaurantName: String): QuerySnapshot =
        suspendCancellableCoroutine { continuation ->
            db.collection(restaurantName).get()
                .addOnSuccessListener { result ->
                    continuation.resume(result)
                }
                .addOnFailureListener { exception ->
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

}