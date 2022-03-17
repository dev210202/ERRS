package jkey20.errs.reservationholder.main

import androidx.activity.view
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import jkey20.errs.R
import jkey20.errs.base.BaseActivity
import jkey20.errs.databinding.ActivityMainBinding

class MainActivity : BaseActivity<ActivityMainBinding, MainViewModel>(
    R.layout.activity_main
) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override val vm: MainViewModel by viewModels()
}