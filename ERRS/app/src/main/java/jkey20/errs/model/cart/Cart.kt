package jkey20.errs.model.cart

import jkey20.errs.model.firebase.Menu
import java.io.Serializable

data class Cart(
    val list : List<Menu> = listOf<Menu>()
) : Serializable
