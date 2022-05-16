package jkey20.errs.activity.reservationholder.util

import android.util.Log
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging

object Util{
    fun getToken() : String{
        var token = ""
        var task = FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.e("message task fail", "!")
                return@OnCompleteListener
            }

            // Get new FCM registration token
            token = task.result
        })
        if(task.isSuccessful){
            token = task.result
        }
        Log.e("token: ", token.toString())
        return token
    }
}