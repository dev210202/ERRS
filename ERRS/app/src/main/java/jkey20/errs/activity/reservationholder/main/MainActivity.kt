package jkey20.errs.activity.reservationholder.main

import android.os.Bundle
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
        vm.getDeviceUUID()

        // esl 인식시 레스토랑 이름가져와서 vm.restaurantName에 값 할당

        vm.readReservation(vm.restaurantName)

        // 예약 데이터 불러오기

        // firebase에 예약 추가
        vm.reservationNumber.collectWithLifecycle(this) { reservationNumber ->
            if (reservationNumber != "") {
                vm.addReservation(
                    vm.restaurantName,
                    Reservation(
                        reservationNumber = reservationNumber,
                    )
                )
            }
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
}