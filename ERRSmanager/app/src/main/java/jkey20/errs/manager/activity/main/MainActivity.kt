package jkey20.errs.manager.activity.main

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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

        vm.setRestaurantName("320")
        vm.getReservationList(vm.loadRestaurantName())
        vm.getRealtimeChanges(vm.loadRestaurantName())

        binding.rvReservation.run {
            setHasFixedSize(true)
            setItemViewCacheSize(10)
            adapter = OrderAdapter(
                onCloseButtonClicked = { reservation ->
                    vm.cancelReservation(vm.loadRestaurantName(), reservation)
                },
                onEntranceButtonClick = { reservation ->
                    vm.notifyEntrance(reservation.token)
                }
            ).apply {
                submitList(vm.loadReservationList())
                addOnScrollListener(object : RecyclerView.OnScrollListener() {
                    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                        super.onScrollStateChanged(recyclerView, newState)
                        if (newState == 0) {
                            val lastVisible =
                                (recyclerView.layoutManager as LinearLayoutManager).findLastCompletelyVisibleItemPosition()
                            Log.e("last Visible", ":" + lastVisible)
                            if (lastVisible != 0) {
                                binding.tvFirstReservation.isVisible = false
                            } else {
                                binding.tvFirstReservation.isVisible = true
                            }
                        }
                    }
                })
            }
        }

        vm.reservationList.collectWithLifecycle(this) { reservationList ->
            if (reservationList.isNotEmpty()) {
                (binding.rvReservation.adapter as OrderAdapter).submitList(reservationList)
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        vm.removeRealTimeUpdate()
        Log.e("REMOVE REALTIME UPDATE", "!!")
    }

}