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
import jkey20.errs.model.cart.CartMenu
import jkey20.errs.model.firebase.Menu
import jkey20.errs.model.firebase.Order
import jkey20.errs.model.firebase.Reservation
import jkey20.errs.repository.collectWithLifecycle

@AndroidEntryPoint
class OrderActivity : BaseActivity<ActivityOrderBinding, MenuViewModel>(
    R.layout.activity_order
) {

    override val vm: MenuViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        // TODO: 모든 액티비티에서 레스토랑 이름 가져오는 메소드 만들어서 받아올것
        vm.setRestaurantName("320")

        vm.checkMyReservation(vm.loadRestaurantName())

        val cartList = intent.getSerializableExtra("cartList") as Cart
        val reservation = vm.loadReservation()
        vm.addCartList(cartList.list)

        binding.rvOrder.run {
            setHasFixedSize(true)
            setItemViewCacheSize(10)
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
            adapter = OrderAdapter()
        }

        binding.btnOrder.setOnClickListener {
           val cartList =  (binding.rvOrder.adapter as OrderAdapter).currentList
            reservation.order.menuList.forEach { reservationMenu ->
                if(cartList.contains(reservationMenu)){
                   val newCartMenu =  cartList[cartList.indexOf(reservationMenu)].copy(count = cartList[cartList.indexOf(reservationMenu)].count + 1)
                   cartList.set(cartList.indexOf(reservationMenu), newCartMenu)
                }
            }
            cartList.forEach { cartMenu ->
                Log.e("ORDER BUtton", cartMenu.toString())
            }


            vm.addOrder(vm.loadRestaurantName(), Order(menuList = cartList, request = binding.etRequest.text.toString()))
        }
        
        vm.cartList.collectWithLifecycle(this) { list ->
            val menuList = mutableListOf<Menu>() // -> 메뉴이름들 저장소
            val cartMenuList = mutableListOf<CartMenu>()
            // 현재 메뉴가 메뉴리스트에 존재한다면
            // 카트메뉴리스트에서 해당 카트메뉴의 카운트를 수정한다.
            //
            list.forEach { menu ->
                if (menuList.contains(menu)) {
                    var newCartMenu = cartMenuList.find { cartMenu -> cartMenu.menu == menu }!!
                    cartMenuList.remove(newCartMenu)
                    cartMenuList.add(newCartMenu.copy(count = newCartMenu.count + 1))
                } else {
                    menuList.add(menu)
                    cartMenuList.add(CartMenu(menu, 1))
                }
            }
            cartMenuList.forEach { cartMenu ->
                Log.e("CART MENU!!!", cartMenu.toString())
            }
            (binding.rvOrder.adapter as OrderAdapter).submitList(cartMenuList)
        }
    }



}