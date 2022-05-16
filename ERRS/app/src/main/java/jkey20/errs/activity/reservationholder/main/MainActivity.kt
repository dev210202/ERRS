package jkey20.errs.activity.reservationholder.main

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.activity.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint
import jkey20.errs.R
import jkey20.errs.activity.reservationholder.menu.MenuActivity
import jkey20.errs.activity.reservationholder.util.Util
import jkey20.errs.base.BaseActivity
import jkey20.errs.databinding.ActivityReservationholderMainBinding
import jkey20.errs.model.firebase.Menu
import jkey20.errs.model.firebase.Order
import jkey20.errs.model.firebase.Reservation
import jkey20.errs.repository.collectWithLifecycle
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityReservationholderMainBinding, MainViewModel>(
    R.layout.activity_reservationholder_main
) {

    override val vm: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


       // setToken()

        vm.setToken(Util.getToken())

        val restaurantName = setRestaurantName()

        vm.setRestaurantName(restaurantName)

        binding.rvOrdersStaus.run {
            setHasFixedSize(true)
            setItemViewCacheSize(10)
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
            adapter = OrdersStatusAdapter(
                onMenuOrderButtonClick = { menu ->
                    // TODO : 메뉴주문취소
                    vm.cancelOrderMenu(vm.loadRestaurantName(), vm.editReservationOrder(menu))
                }
            ).apply {
                submitList(vm.loadReservation().order.menuList)
            }
        }

        vm.restaurantName.collectWithLifecycle(this) {
            vm.checkMyReservation(vm.loadRestaurantName()) // TODO: 1. 예약 확인 -> 내 예약이 있는지 여부, 전체 예약 리스트 가짐
            vm.addRealtimeUpdate(vm.loadRestaurantName())
        }

        vm.isReserved.collectWithLifecycle(this) { isReserved -> // TODO: 2.  예약이 있는지 여부에 따라 동작
            if (isReserved.equals("false")) {
                vm.createReservation()
            }
        }

        vm.reservation.collectWithLifecycle(this) { reservation ->
            if(!reservation.equals(Reservation())) {
                (binding.rvOrdersStaus.adapter as OrdersStatusAdapter).submitList(reservation.order.menuList)
                if (vm.getIsReserved().equals("false")) {
                    vm.addReservation(vm.loadRestaurantName(), vm.loadReservation())
                } else {
                    vm.addMyWaitingNumber()
                }
            }
        }

        binding.btnReservationCancel.setOnClickListener {
            vm.cancelReservation(vm.loadRestaurantName())
            finish()
            // TODO: 예약취소이후 앱 종료
        }

        binding.btnOrder.setOnClickListener {
            val intent = Intent(this, MenuActivity::class.java)
            intent.putExtra("restaurantName", restaurantName)
            startActivity(intent)
        }
    }

    private fun setRestaurantName() : String{
        // RETURN QR INFO
        return "320"
    }

    override fun onDestroy() {
        super.onDestroy()
        vm.removeRealTimeUpdate()
        Log.e("REMOVE REALTIME UPDATE", "!!")
    }
}