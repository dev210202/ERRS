package jkey20.errs.model.cart

import jkey20.errs.model.firebase.Menu

data class CartMenu(
    val menu : Menu = Menu(),
    val count : Int = 0
)
