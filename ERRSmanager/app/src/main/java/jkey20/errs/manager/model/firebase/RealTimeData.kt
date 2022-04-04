package jkey20.errs.manager.model.firebase

import jkey20.errs.model.firebase.Reservation

data class RealTimeData(
    val type : String,
    val reservation : Reservation
)
