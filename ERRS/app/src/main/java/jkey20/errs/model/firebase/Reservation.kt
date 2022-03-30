package jkey20.errs.model.firebase

data class Reservation(
    val reservationNumber: String = "",
    val order: Order = Order(),
    val time: String = ""
)
