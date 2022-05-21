package jkey20.errs.activity.reservationholder.splash

import android.util.Log
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import jkey20.errs.activity.reservationholder.util.Util
import jkey20.errs.base.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class SplashViewModel : BaseViewModel() {

    private val _deviceToken = MutableStateFlow(String())
    val deviceToken = _deviceToken.asStateFlow()

    fun setDeviceToken(token : String){
        _deviceToken.value = token

    }

    fun getDeviceToken(): String {
        return _deviceToken.value
    }
}