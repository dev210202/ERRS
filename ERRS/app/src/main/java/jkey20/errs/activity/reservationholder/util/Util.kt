package jkey20.errs.activity.reservationholder.util

import android.util.Log
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging

object Util{

    var deviceToken =""

    fun getToken() : String{
        if(deviceToken == "") {
            var task =
                FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        Log.e("message task fail", "!")
                        return@OnCompleteListener
                    }

                    // Get new FCM registration token
                    deviceToken = task.result
                })
            if (task.isSuccessful) {
                deviceToken = task.result
            }
            Log.e("token: ", deviceToken.toString())
        }
        return deviceToken
    }
}