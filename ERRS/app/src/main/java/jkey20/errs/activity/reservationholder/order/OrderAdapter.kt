package jkey20.errs.activity.reservationholder.order

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import jkey20.errs.databinding.ItemOrderBinding
import jkey20.errs.model.cart.CartMenu
import jkey20.errs.model.firebase.Menu

class OrderAdapter : ListAdapter<CartMenu, OrderAdapter.ViewHolder>(diffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemOrderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(
        private val binding: ItemOrderBinding
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(cartMenu: CartMenu) {
            // Log.e("BIND!!", menu.toString())
            binding.cartMenu = cartMenu
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