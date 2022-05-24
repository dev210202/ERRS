package jkey20.errs.activity.reservationholder.order

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import dagger.hilt.android.AndroidEntryPoint
import jkey20.errs.R
import jkey20.errs.activity.reservationholder.main.MainViewModel
import jkey20.errs.activity.reservationholder.menu.MenuViewModel
import jkey20.errs.activity.reservationholder.util.Util
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

        vm.setToken(Util.getToken())
        lateinit var cartList: Cart





        binding.rvOrder.run {
            setHasFixedSize(true)
            setItemViewCacheSize(10)
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
            adapter = OrderAdapter()
        }

        binding.btnOrder.setOnClickListener {
            val orderList = vm.loadReservation().order.menuList.toMutableList()
            val cartList = (binding.rvOrder.adapter as OrderAdapter).currentList.toMutableList()
            val tmpList = mutableListOf<Menu>()



            cartList.forEach { cartMenu ->
                tmpList.add(cartMenu.menu)
            }

            // 오더 리스트에 cartList에 있는 값중 같은게 있는지 찾고 있다면 combineList에 해당 메뉴의 카운트값을 늘리고 cartList에서 삭제.
            orderList.forEach { reservationMenu ->
                Log.e("RESERVATION MENU", reservationMenu.menu.toString())
                tmpList.forEach { menu ->
                    Log.e("TMP MENU", menu.toString())
                }
                if (tmpList.contains(reservationMenu.menu)) {
                    orderList.set(
                        orderList.indexOf(reservationMenu),
                        reservationMenu.copy(
                            count = reservationMenu.count + cartList[tmpList.indexOf(reservationMenu.menu)].count
                        )
                    )
                    tmpList.remove(reservationMenu.menu)
                }
            }
            tmpList.forEach { menu ->
                orderList.add(CartMenu(menu, count = 1))
            }
            orderList.forEach { cartMenu ->
                Log.e("cartList", cartMenu.toString())
            }

            cartList.forEach { cartMenu ->
                Log.e("cartList", cartMenu.toString())
            }
            tmpList.forEach { menu ->
                Log.e("tmpList", menu.toString())
            }

            vm.addOrder(
                vm.loadRestaurantName(),
                Order(menuList = orderList, request = " " + binding.etRequest.text.toString())
            )

            Toast.makeText(this, "주문이 접수됐습니다.",Toast.LENGTH_SHORT).show()
        }

        vm.deviceToken.collectWithLifecycle(this) { deviceToken ->
            cartList = intent.getSerializableExtra("cartList") as Cart // 정렬이 안된 리스트
            vm.addCartList(cartList.list)
        }
        vm.restaurantName.collectWithLifecycle(this) { restaurantName ->
            vm.checkMyReservation(restaurantName)
        }
        vm.reservation.collectWithLifecycle(this) {
            Log.e("reservation collec", it.toString())

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
            vm.setRestaurantName("320")

        }
    }


}