package jkey20.errs.model.firebase

data class Order(
    var menuList : List<Menu> = listOf(),
    val request : String = "",
)
