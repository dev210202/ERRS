package jkey20.errs.manager.activity.main

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import jkey20.errs.manager.databinding.ItemReservationBinding
import jkey20.errs.model.firebase.Reservation

class OrderAdapter(private val onCloseButtonClicked: (Reservation) -> Unit) :
    ListAdapter<Reservation, OrderAdapter.ViewHolder>(diffUtil) {

    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val binding =
            ItemReservationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, onCloseButtonClicked)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), position)

    }


    class ViewHolder(
        private val binding: ItemReservationBinding,
        private val onCloseButtonClicked: (Reservation) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(reservation: Reservation, position: Int) {
            binding.rvOrder.adapter = MenuAdapter().apply {
                submitList(reservation.order.menuList)
            }
            binding.btnClose.setOnClickListener {
                onCloseButtonClicked(reservation)
            }
            binding.reservation = reservation
        }
    }

    private companion object {
        val diffUtil = object : DiffUtil.ItemCallback<Reservation>() {
            override fun areContentsTheSame(oldItem: Reservation, newItem: Reservation) =
                oldItem == newItem

            override fun areItemsTheSame(oldItem: Reservation, newItem: Reservation) =
                oldItem.reservationNumber == newItem.reservationNumber
        }
    }

}