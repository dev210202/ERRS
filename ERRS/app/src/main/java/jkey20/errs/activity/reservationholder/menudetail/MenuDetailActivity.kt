package jkey20.errs.activity.reservationholder.menudetail

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import jkey20.errs.R
import jkey20.errs.base.BaseActivity
import jkey20.errs.databinding.ActivityMenuDetailBinding
import jkey20.errs.model.firebase.Menu

class MenuDetailActivity : BaseActivity<ActivityMenuDetailBinding, MenuDetailViewModel>(
    R.layout.activity_menu_detail
) {

    override val vm: MenuDetailViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val menu = intent.getSerializableExtra("menu") as Menu
        binding.menu = menu

    }

}