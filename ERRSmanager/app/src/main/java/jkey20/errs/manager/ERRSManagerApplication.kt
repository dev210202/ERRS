package jkey20.errs.manager

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class ERRSManagerApplication : Application(){
    override fun onCreate() {
        super.onCreate()
    }
}