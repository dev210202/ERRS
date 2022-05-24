package jkey20.errs.activity.reservationholder.main

import android.app.AlertDialog
import android.app.Dialog
import android.app.ProgressDialog
import android.content.DialogInterface
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

        vm.setToken(Util.getToken())

        vm.setRestaurantName("320")

        binding.rvOrdersStaus.run {
            setHasFixedSize(true)
            setItemViewCacheSize(10)
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
            adapter = OrdersStatusAdapter(
                onMenuOrderRemoveButtonClicked = { cartMenu ->
                    // TODO : 메뉴주문취소
                    vm.cancelOrderMenu(vm.loadRestaurantName(), vm.editReservationOrder(cartMenu))
                }
            ).apply {
                submitList(vm.loadReservation().order.menuList)
            }
        }

        binding.btnReservationCancel.setOnClickListener {
            vm.cancelReservation(vm.loadRestaurantName())
            finish()
            // TODO: 예약취소이후 앱 종료
        }

        binding.btnOrder.setOnClickListener {
            val intent = Intent(this, MenuActivity::class.java)
            val restaurantName = vm.loadRestaurantName()
            intent.putExtra("restaurantName", restaurantName)
            startActivity(intent)
        }

        vm.deviceToken.collectWithLifecycle(this){
            val restaurantName = intent.getStringExtra("qrRestaurantName")
            if (restaurantName != null) {
                vm.setRestaurantName(restaurantName)
            }
            Log.e("deviceToken CWC","!!")
        }

        vm.restaurantName.collectWithLifecycle(this) {
            vm.checkMyReservation(vm.loadRestaurantName()) // TODO: 1. 예약 확인 -> 내 예약이 있는지 여부, 전체 예약 리스트 가짐

            Log.e("restaurantName CWC","!!")
        }

        vm.isReserved.collectWithLifecycle(this) { isReserved -> // TODO: 2.  예약이 있는지 여부에 따라 동작
            Log.e("isReserved VALUE","" +isReserved+"!!")
            if (isReserved.equals("false")) {
                vm.createReservation()
            }
            //vm.addRealtimeUpdate(vm.loadRestaurantName())
            Log.e("isReserved CWC","!!")
        }

        vm.reservation.collectWithLifecycle(this) { reservation ->
            Log.e("reservation !@#!@#",reservation.toString())
            if(!reservation.equals(Reservation())) {
                (binding.rvOrdersStaus.adapter as OrdersStatusAdapter).submitList(reservation.order.menuList)
                if (vm.getIsReserved().equals("false")) {
                    vm.addReservation(vm.loadRestaurantName(), vm.loadReservation())
                } else {
                    if(reservation.reservationNumber != "") {
                        vm.addMyWaitingNumber()
                    }
                }
            }
            vm.addRealtimeUpdate(vm.loadRestaurantName())
            Log.e("reservation CWC","!!")

        }

        vm.isCanceled.collectWithLifecycle(this) { isCanceled ->
            if(isCanceled){
                showDialog()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        vm.removeRealTimeUpdate()
        Log.e("REMOVE REALTIME UPDATE", "!!")
    }

    fun showDialog(){
        val dialog = AlertDialog.Builder(this)
        dialog.setTitle("예약이 취소되었습니다.")
        dialog.setMessage("가게에 문의하시거나 다시 예약을 진행해주세요.")
        dialog.setNegativeButton("확인", object : DialogInterface.OnClickListener{
            override fun onClick(dialog: DialogInterface?, which: Int) {
                finish()
            }

        })
        dialog.show()
    }

}