package jkey20.errs.model.firebase

import java.io.Serializable

data class Menu(
    val name: String = "",
    val price: Int = 0,
    val status: String = "",
    val info: String = "",
    val uri: String = ""
) : Serializable
