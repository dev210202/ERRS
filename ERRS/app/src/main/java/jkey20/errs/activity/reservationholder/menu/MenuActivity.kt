package jkey20.errs.activity.reservationholder.menu

import android.os.Bundle
import androidx.activity.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import jkey20.errs.R
import jkey20.errs.activity.reservationholder.main.OrdersStatusAdapter
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
            ).apply {
                submitList(vm.loadMenuList())
            }
        }

        vm.menuList.collectWithLifecycle(this) { menuList ->
            (binding.rvMenu.adapter as MenuAdapter).submitList(menuList)
        }

    }
}