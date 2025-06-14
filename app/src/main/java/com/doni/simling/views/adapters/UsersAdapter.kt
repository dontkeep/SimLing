package com.doni.simling.views.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.doni.simling.databinding.ItemUsersBinding
import com.doni.simling.models.connections.responses.DataItemUser

class UsersAdapter(
    private val onItemClick: (DataItemUser) -> Unit
) : PagingDataAdapter<DataItemUser, UsersAdapter.ViewHolder>(DIFF_CALLBACK) {

    inner class ViewHolder(private val binding: ItemUsersBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(user: DataItemUser) {
            binding.tvName.text = user.name
            binding.tvAddress.text = user.address

            binding.root.setOnClickListener {
                onItemClick(user)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemUsersBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<DataItemUser>() {
            override fun areItemsTheSame(oldItem: DataItemUser, newItem: DataItemUser): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: DataItemUser, newItem: DataItemUser): Boolean {
                return oldItem == newItem
            }
        }
    }
}
