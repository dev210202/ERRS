package jkey20.errs.activity.reservationholder.menu

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import dagger.hilt.android.AndroidEntryPoint
import jkey20.errs.R
import jkey20.errs.activity.reservationholder.menudetail.MenuDetailActivity
import jkey20.errs.base.BaseActivity
import jkey20.errs.databinding.ActivityMenuBinding
import jkey20.errs.repository.collectWithLifecycle

@AndroidEntryPoint
class MenuActivity : BaseActivity<ActivityMenuBinding, MenuViewModel>(
    R.layout.activity_menu
) {
    override val vm: MenuViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        vm.addMenuList()

        binding.rvMenu.run {
            setHasFixedSize(true)
            setItemViewCacheSize(10)
            addItemDecoration(MenuItemDecoration(context))
            adapter = MenuAdapter(
                onMenuClick = { menu ->
                    val intent =  Intent(this@MenuActivity, MenuDetailActivity::class.java)
                    intent.putExtra("menu", menu)
                    startActivity(intent)
                }
            ).apply {
                submitList(vm.loadMenuList())
            }
        }

        vm.menuList.collectWithLifecycle(this) { menuList ->
            (binding.rvMenu.adapter as MenuAdapter).submitList(menuList)
        }

    }
}