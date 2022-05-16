package jkey20.errs.activity.reservationholder.order

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import dagger.hilt.android.AndroidEntryPoint
import jkey20.errs.R
import jkey20.errs.activity.reservationholder.main.MainViewModel
import jkey20.errs.activity.reservationholder.menu.MenuViewModel
import jkey20.errs.base.BaseActivity
import jkey20.errs.databinding.ActivityOrderBinding
import jkey20.errs.model.cart.Cart
import jkey20.errs.model.firebase.Menu
import jkey20.errs.repository.collectWithLifecycle

@AndroidEntryPoint
class OrderActivity : BaseActivity<ActivityOrderBinding, MenuViewModel>(
    R.layout.activity_order
) {

    override val vm: MenuViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val cartList = intent.getSerializableExtra("cartList") as Cart
        vm.addCartList(cartList.list)

        binding.rvOrder.run {
            setHasFixedSize(true)
            setItemViewCacheSize(10)
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
            adapter = OrderAdapter(

            ).apply {
                submitList(cartList.list)
                notifyDataSetChanged()
            }
        }
        (binding.rvOrder.adapter as OrderAdapter).submitList(cartList.list)
        vm.cartList.collectWithLifecycle(this){ list ->
            (binding.rvOrder.adapter as OrderAdapter).submitList(list)
        }
    }

    private fun sortCartList(list : List<Menu>) {

    }

}