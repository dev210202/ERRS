package jkey20.errs

import android.app.Application
import android.util.Log
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class ERRSAplication : Application() {
    override fun onCreate() {
        super.onCreate()
    }
}