package jkey20.errs.model.firebase

data class Order(
    val menuList : List<Menu> = listOf(),
    val request : String = "",
)
