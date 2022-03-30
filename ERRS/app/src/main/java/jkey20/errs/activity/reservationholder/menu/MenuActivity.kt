package jkey20.errs.activity.reservationholder.menu

import android.os.Bundle
import androidx.activity.viewModels
import jkey20.errs.R
import jkey20.errs.base.BaseActivity
import jkey20.errs.databinding.ActivityMenuBinding

class MenuActivity : BaseActivity<ActivityMenuBinding, MenuViewModel >(
    R.layout.activity_menu
) {
    override val vm: MenuViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



    }
}