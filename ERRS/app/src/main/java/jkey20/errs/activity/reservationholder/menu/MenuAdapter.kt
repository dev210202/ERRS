package jkey20.errs.activity.reservationholder.menu

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import jkey20.errs.databinding.ItemMenuInfoBinding
import jkey20.errs.model.firebase.Menu

class MenuAdapter(private val onMenuClick: (Menu) -> Unit) :
    ListAdapter<Menu, MenuAdapter.ViewHolder>(diffUtil) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemMenuInfoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, onMenuClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(
        private val binding: ItemMenuInfoBinding,
        private val onMenuClick: (Menu) -> Unit
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(menu: Menu) {
            binding.menu = menu
            binding.layoutMenu.setOnClickListener {
                // 메뉴 자세히 보기
                onMenuClick(menu)
            }
            Glide.with(binding.root.context).load(menu.uri).centerCrop().into(binding.ivMenu)

        }
    }

    private companion object {
        val diffUtil = object : DiffUtil.ItemCallback<Menu>() {
            override fun areContentsTheSame(oldItem: Menu, newItem: Menu) =
                oldItem == newItem

            override fun areItemsTheSame(oldItem: Menu, newItem: Menu) =
                oldItem == newItem
        }
    }

}