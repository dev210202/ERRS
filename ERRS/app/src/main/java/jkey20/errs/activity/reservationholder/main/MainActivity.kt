package jkey20.errs.activity.reservationholder.main

import android.os.Bundle
import androidx.activity.viewModels
import dagger.hilt.android.AndroidEntryPoint
import jkey20.errs.R
import jkey20.errs.base.BaseActivity
import jkey20.errs.databinding.ActivityReservationholderMainBinding

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityReservationholderMainBinding, MainViewModel>(
    R.layout.activity_reservationholder_main
) {

    override val vm: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // firebase 연동

    }

}