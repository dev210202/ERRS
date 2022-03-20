package jkey20.errs.model.firebase

data class Order(
    val menu : Menu = Menu(),
    val request : String = "",
)
