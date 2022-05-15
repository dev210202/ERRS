package jkey20.errs.activity.reservationholder.menudetail

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.view.menu.MenuView
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import jkey20.errs.R
import jkey20.errs.activity.reservationholder.menu.MenuViewModel
import jkey20.errs.base.BaseActivity
import jkey20.errs.databinding.ActivityMenuDetailBinding
import jkey20.errs.model.firebase.Menu

@AndroidEntryPoint
class MenuDetailActivity : BaseActivity<ActivityMenuDetailBinding, MenuViewModel>(
    R.layout.activity_menu_detail
) {

    override val vm: MenuViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val menu = intent.getSerializableExtra("menu") as Menu
        binding.menu = menu
        Glide.with(this).load(menu.uri).into(binding.ivMenu)

        binding.btnOrder.setOnClickListener {
            vm.addCartList(menu)
        }
    }

}