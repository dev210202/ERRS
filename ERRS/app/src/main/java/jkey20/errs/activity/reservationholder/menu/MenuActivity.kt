package jkey20.errs.activity.reservationholder.menu

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import dagger.hilt.android.AndroidEntryPoint
import jkey20.errs.R
import jkey20.errs.activity.reservationholder.menudetail.MenuDetailActivity
import jkey20.errs.activity.reservationholder.order.OrderActivity
import jkey20.errs.base.BaseActivity
import jkey20.errs.databinding.ActivityMenuBinding
import jkey20.errs.model.cart.Cart
import jkey20.errs.model.firebase.Reservation
import jkey20.errs.repository.collectWithLifecycle


/*
  1. 식당 이름 가져옴
  2. 메뉴이미지 리스트 로딩
  3. 메뉴정보 리스트 로딩
  4. recyclerview에 데이터 반영
 */
@AndroidEntryPoint
class MenuActivity : BaseActivity<ActivityMenuBinding, MenuViewModel>(
    R.layout.activity_menu
) {
    override val vm: MenuViewModel by viewModels()
    lateinit var launcher : ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        vm.setRestaurantName(intent.getStringExtra("restaurantName")!!)
        vm.addMenuImageList(vm.loadRestaurantName())
        vm.addMenuList(vm.loadRestaurantName())

        launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            if(it.resultCode == RESULT_OK){
                val cart = it.data?.getSerializableExtra("cart") as Cart
                vm.addCartList(cart.list)
            }
        }

        binding.rvMenu.run {
            setHasFixedSize(true)
            setItemViewCacheSize(10)
            addItemDecoration(MenuItemDecoration(context))
            adapter = MenuAdapter(
                onMenuClick = { menu ->
                    val intent = Intent(this@MenuActivity, MenuDetailActivity::class.java)
                    intent.putExtra("menu", menu)

                    launcher.launch(intent)
                }
            ).apply {
                submitList(vm.loadMenuList())
            }
        }

        binding.tvCartCount.setOnClickListener {
            val intent = Intent(this, OrderActivity::class.java)
            val cart = Cart(list = vm.loadCartList())
            intent.putExtra("cartList", cart)
            startActivity(intent)
        }

        vm.uriList.collectWithLifecycle(this) { menuList ->
            (binding.rvMenu.adapter as MenuAdapter).submitList(
                vm.getMenuList(
                    vm.loadUriList(),
                    vm.loadMenuList()
                )
            )
        }
    }
}