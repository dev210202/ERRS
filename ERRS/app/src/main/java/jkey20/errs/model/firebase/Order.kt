package jkey20.errs.model.firebase

import jkey20.errs.model.cart.CartMenu
import java.io.Serializable

data class Order(
    var menuList : List<CartMenu> = listOf(),
    val request : String = "",
) : Serializable
