package jkey20.errs.activity.reservationholder.main

import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.activity.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import jkey20.errs.R
import jkey20.errs.base.BaseActivity
import jkey20.errs.databinding.ActivityReservationholderMainBinding
import jkey20.errs.model.firebase.Menu
import jkey20.errs.model.firebase.Order
import jkey20.errs.model.firebase.Reservation
import jkey20.errs.repository.collectWithLifecycle

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityReservationholderMainBinding, MainViewModel>(
    R.layout.activity_reservationholder_main
) {

    override val vm: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 기기 UUID 불러와서 vm.userUUID에 값 할당
        vm.setDeviceUUID(getUUID())
        // esl 인식시 레스토랑 이름가져와서 vm.restaurantName에 값 할당

        vm.setRestaurantName("320")

        // 값 할당시 예약 데이터 불러옴
        vm.restaurantName.collectWithLifecycle(this){ restaurantName ->
            if(restaurantName.isNotEmpty()){
                vm.readReservation(restaurantName)
                vm.addRealtimeWaitingTeamsUpdate(restaurantName)
            }
        }

        // firebase에 예약 추가
        vm.reservationNumber.collectWithLifecycle(this) { reservationNumber ->
            if (reservationNumber.isNotEmpty()) {
                vm.addReservation(
                    vm.getRestaurantName(),
                    Reservation(
                        reservationNumber = reservationNumber,
                    )
                )
            }
        }

        binding.btnOrder.setOnClickListener {
            vm.addReservation(
                vm.getRestaurantName(),
                Reservation(
                    reservationNumber = vm.getMyWaitingTeamsNumber(),
                )
            )
        }

//        vm.addReservation(
//            "restaurantName1",
//            Reservation(
//                reservationNumber = "1",
//                Order(
//                    Menu(
//                        name = "삼각김밥",
//                        price = 3000,
//                        pay = "완료",
//                        option = listOf()
//                    ),
//                    request = "요청사항"
//                )
//            )
//        )

    }

    private fun getUUID() : String{
        return android.provider.Settings.Secure.getString(this.contentResolver, Settings.Secure.ANDROID_ID)
    }
}