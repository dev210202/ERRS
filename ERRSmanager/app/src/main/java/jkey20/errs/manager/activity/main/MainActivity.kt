package jkey20.errs.manager.activity.main

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import dagger.hilt.android.AndroidEntryPoint
import jkey20.errs.base.BaseActivity
import jkey20.errs.manager.R
import jkey20.errs.manager.databinding.ActivityMainBinding
import jkey20.errs.manager.util.collectWithLifecycle

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding, MainViewModel>(
    R.layout.activity_main
) {

    override val vm: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        vm.getReservationList("320")
        vm.getRealtimeChanges("320")
        binding.rvReservation.run {
            setHasFixedSize(true)
            setItemViewCacheSize(10)

            adapter = OrderAdapter().apply {
                Log.e("APPLY", "!!")
                submitList(vm.loadReservationList())
            }
        }

        vm.reservationList.collectWithLifecycle(this) { reservationList ->

            if (reservationList.isNotEmpty()) {
                (binding.rvReservation.adapter as OrderAdapter).submitList(reservationList)
                Log.e("reservation", "!!")
            }
        }

    }

}