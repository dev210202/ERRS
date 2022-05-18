package jkey20.errs.activity.reservationholder.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import jkey20.errs.databinding.ItemMenuOrderBinding
import jkey20.errs.model.cart.CartMenu
import jkey20.errs.model.firebase.Menu

class OrdersStatusAdapter(
    private val onMenuOrderButtonClick : (CartMenu) -> Unit
) : ListAdapter<CartMenu, OrdersStatusAdapter.ViewHolder>(diffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemMenuOrderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, onMenuOrderButtonClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }


    class ViewHolder(
        private val binding: ItemMenuOrderBinding,
        private val onMenuOrderButtonClick : (CartMenu) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(cartMenu: CartMenu) {
            binding.cartMenu = cartMenu
            binding.btnMenuStatus.setOnClickListener {
                onMenuOrderButtonClick(cartMenu)
            }
        }
    }

    private companion object {
        val diffUtil = object : DiffUtil.ItemCallback<CartMenu>() {
            override fun areContentsTheSame(oldItem: CartMenu, newItem: CartMenu) =
                oldItem == newItem

            override fun areItemsTheSame(oldItem: CartMenu, newItem: CartMenu) =
                oldItem == newItem
        }
    }


}