package jkey20.errs.manager.repository

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import jkey20.errs.manager.util.toObjectNonNull
import jkey20.errs.model.firebase.Reservation
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class FirebaseRepository {

    val db = Firebase.firestore

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