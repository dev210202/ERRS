package jkey20.errs.repository

import android.net.Uri
import android.util.Log
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import jkey20.errs.model.firebase.Menu
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
    val storage = Firebase.storage
    val registrationList = mutableListOf<ListenerRegistration>()

    @ExperimentalCoroutinesApi
    suspend fun createReservation(
        restaurantName: String,
        reservation: Reservation,
        token: String
    ): Boolean =
        suspendCancellableCoroutine { continuation ->
            db.collection(restaurantName). // 식당이름 컬렉션
            document(token). // token 문서
            set(reservation) // 예약정보 필드
                .addOnSuccessListener {
                    continuation.resume(true)
                }
                .addOnFailureListener { exception ->
                    continuation.resume(false)
                    throw exception
                }
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


    suspend fun deleteReservation(restaurantName: String, token: String): Boolean =
        suspendCancellableCoroutine { continuation ->
            db.collection(restaurantName).document(token).delete().addOnSuccessListener {
                continuation.resume(true)
            }.addOnFailureListener { exception ->
                continuation.resume(false)
                throw  exception
            }
        }


    suspend fun readRealtimeUpdate(restaurantName: String) = callbackFlow {
        var registration = db.collection(restaurantName).addSnapshotListener { value, error ->
            if (value != null) {
                Log.e("READ REAL TIME UPDATE", "!")
                trySend(value)
            }
            if (error != null) {
                throw error
            }
        }
        registrationList.add(registration)
        awaitClose()
    }

    suspend fun deleteOrderMenu(restaurantName: String, token: String, order: Order): Boolean =
        suspendCancellableCoroutine { continuation ->
            db.collection(restaurantName).document(token)
                .update("order", order).addOnSuccessListener {
                    continuation.resume(true)
                }.addOnFailureListener { exception ->
                    continuation.resume(false)
                    throw  exception
                }
        }


    suspend fun readMenuInfos(restaurantName: String): DocumentSnapshot =
        suspendCancellableCoroutine { continuation ->
            db.collection("menu").document(restaurantName).get().addOnSuccessListener { documentSnapshot ->
                continuation.resume(documentSnapshot)
            }.addOnFailureListener { exception ->
                throw exception
            }
        }

    suspend fun readMenuImages(restaurantName: String): List<Uri> =
        suspendCancellableCoroutine { continuation ->
            val imageUriList = mutableListOf<Uri>()
            val storageRef = storage.reference.child(restaurantName)
            storageRef.listAll().addOnSuccessListener { listResult ->
                listResult.items.forEach { item ->
                    item.downloadUrl.addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            imageUriList.add(task.result)
                            if (listResult.items.size == imageUriList.size) {
                                continuation.resume(imageUriList)
                            }
                        } else {
                            // task fail
                        }
                    }.addOnFailureListener { exception ->
                        // item.downloadUrl fail
                    }
                }

            }.addOnFailureListener { exception ->
                // storageRef.listAll fail
            }
        }


    fun removeRegistrationList() {
        registrationList.forEach {
            it.remove()
        }
    }
}