package jkey20.errs.manager.repository

import android.util.Log
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import jkey20.errs.manager.BuildConfig
import jkey20.errs.manager.util.toObjectNonNull
import jkey20.errs.model.firebase.Reservation
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import org.json.JSONObject
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets
import kotlin.coroutines.resume

class FirebaseRepository {

    val db = Firebase.firestore
    val registrationList = mutableListOf<ListenerRegistration>()

    suspend fun readRealtimeUpdate(restaurantName: String) =
        callbackFlow {
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

    suspend fun deleteReservation(restaurantName: String, uuid: String): Boolean =
        suspendCancellableCoroutine { continuation ->
            db.collection(restaurantName).document(uuid).delete()
                .addOnSuccessListener {
                    continuation.resume(true)
                }.addOnFailureListener { exception ->
                    continuation.resume(false)
                    throw  exception
                }
        }

    suspend fun notifyEntrance(token: String) : Boolean =
        suspendCancellableCoroutine { continuation ->
            try{
                var notificationObject = JSONObject()
                var root = JSONObject()
                var fcmServer = BuildConfig.FCM_KEY

                notificationObject.put("body", "body")
                notificationObject.put("title", "title")
                root.put("notification", notificationObject)
                root.put("to", token)

                var url = URL("https://fcm.googleapis.com/fcm/send")
                var conn = url.openConnection() as HttpURLConnection

                conn.requestMethod = "POST"
                conn.doOutput = true
                conn.doInput = true
                conn.addRequestProperty("Authorization", "key=" + fcmServer)
                conn.setRequestProperty("Accept", "application/json")
                conn.setRequestProperty("Content-type", "application/json")

                var os = conn.outputStream
                os.write(root.toString().toByteArray(StandardCharsets.UTF_8))
                os.flush()
                conn.responseCode
                continuation.resume(true)
            }catch (exception : Exception){
                Log.e("EXCEPTION", exception.toString())
                throw exception
                continuation.resume(false)
            }

        }

    fun removeRegistrationList() {
        registrationList.forEach {
            it.remove()
        }
    }
}