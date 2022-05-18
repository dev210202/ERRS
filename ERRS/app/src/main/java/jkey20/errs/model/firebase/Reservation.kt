package jkey20.errs.model.firebase

import java.io.Serializable

data class Reservation(
    val reservationNumber: String = "",
    val order: Order = Order(),
    val time: String = "",
    val token: String = ""
) : Serializable
