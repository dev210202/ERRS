package jkey20.errs.model.firebase

data class Reservation(
    val reservationNumber : Int = 0,
    val order : Order = Order()
)
