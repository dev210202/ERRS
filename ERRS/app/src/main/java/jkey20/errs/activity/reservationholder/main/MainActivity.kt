package jkey20.errs.activity.reservationholder.main

import android.os.Bundle
import androidx.activity.viewModels
import dagger.hilt.android.AndroidEntryPoint
import jkey20.errs.R
import jkey20.errs.base.BaseActivity
import jkey20.errs.databinding.ActivityReservationholderMainBinding
import jkey20.errs.model.firebase.Menu
import jkey20.errs.model.firebase.Order
import jkey20.errs.model.firebase.Reservation

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityReservationholderMainBinding, MainViewModel>(
    R.layout.activity_reservationholder_main
) {

    override val vm: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // esl 인식시 레스토랑 이름 + 레스토랑 고유 id로 인증?

        // 예약 데이터 불러오기

        // firebase에 예약 추가
        vm.addReservation(
            Reservation(
                reservationNumber = 1,
                Order(
                    Menu(
                        name = "삼각김밥",
                        price = 3000,
                        pay = "완료",
                        option = listOf()
                    ),
                    request = "요청사항"
                )
            )
        )

    }
}