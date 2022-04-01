package jkey20.errs.manager.repository

import android.util.Log
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import jkey20.errs.manager.util.toObjectNonNull
import jkey20.errs.model.firebase.Reservation
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class FirebaseRepository {

    val db = Firebase.firestore


    suspend fun readRealtimeChanges(restaurantName: String, reservationList: List<Reservation>) =
        callbackFlow {
            try {
                db.collection(restaurantName).addSnapshotListener { value, error ->
                    if (value != null) {
                        for (dc in value.documentChanges) {
                            val list = reservationList.toMutableList()
                            when (dc.type) {

                                DocumentChange.Type.ADDED -> {

                                    list.add(dc.document.toObject(Reservation::class.java))
                                    // reservationList.add()
                                    Log.e("REAL TIME DATA CHANGED", "ADDED")
                                    trySend(list.toList())
                                }
                                DocumentChange.Type.MODIFIED -> {
                                    Log.e("REAL TIME DATA MODIFIED", "MODIFIED")
                                }
                                DocumentChange.Type.REMOVED -> {
                                    list.remove(dc.document.toObject(Reservation::class.java))
                                    Log.e("REAL TIME DATA REMOVED", "REMOVED")
                                    trySend(list.toList())
                                }
                            }
                        }
                    }
                    if (error != null) {
                        throw error
                    }
                }
            } catch (exception: Exception) {
                // trySend(exception.toString())
            }
            awaitClose()
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