package jkey20.errs.activity.reservationholder.splash

import android.content.Intent
import android.os.*
import androidx.appcompat.app.AppCompatActivity
import android.text.SpannableStringBuilder
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
import jkey20.errs.R
import jkey20.errs.activity.reservationholder.main.MainActivity
import jkey20.errs.activity.reservationholder.util.Util
import jkey20.errs.base.BaseActivity
import jkey20.errs.databinding.ActivitySplashBinding
import jkey20.errs.repository.collectWithLifecycle

class SplashActivity : BaseActivity<ActivitySplashBinding, SplashViewModel>(
    R.layout.activity_splash
) {

    override val vm: SplashViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        vm.setDeviceToken(Util.getToken())
        val startTime = System.currentTimeMillis()
        val uiOptions = window.decorView.systemUiVisibility
        var newUiOptions = uiOptions
        val isImmersiveModeEnabled =
            uiOptions or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY == uiOptions
        newUiOptions = newUiOptions xor View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        newUiOptions = newUiOptions xor View.SYSTEM_UI_FLAG_FULLSCREEN
        newUiOptions = newUiOptions xor View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        window.decorView.systemUiVisibility = newUiOptions

        binding.ivRefresh.setOnClickListener {
            vm.setDeviceToken(Util.getToken())
        }

        vm.deviceToken.collectWithLifecycle(this){ token ->
            Log.e("COLLECT TOEKN", token)
            if(token.isNotEmpty()){
                startLoading()
            }
        }

        val countTimer = object : CountDownTimer(100000, 100000){
            override fun onTick(millisUntilFinished: Long) {
                if(vm.getDeviceToken().isEmpty()){
                    vm.setDeviceToken(Util.getToken())
                }
            }

            override fun onFinish() {
                binding.ivRefresh.visibility = View.VISIBLE
            }

        }.start()
    }

    private fun startLoading() {
        val handler = Handler()
        handler.postDelayed({
            finish()
            val gotoMain = Intent(this, MainActivity::class.java)
            val token = vm.getDeviceToken()
            intent.putExtra("token", token)
            startActivity(gotoMain)
        }, 2000)
    }


}