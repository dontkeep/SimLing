package com.doni.simling.views.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.doni.simling.models.connections.responses.DataItemSecurityByUser
import com.doni.simling.databinding.ItemPresenceBinding
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

class SecurityRecordByUserAdapter(
    private val onItemClick: (DataItemSecurityByUser) -> Unit
) : ListAdapter<DataItemSecurityByUser, SecurityRecordByUserAdapter.ViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemPresenceBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    inner class ViewHolder(private val binding: ItemPresenceBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: DataItemSecurityByUser) {
            binding.tvUser.text = item.securityName ?: "Unknown User"
            binding.tvDate.text = formatDate(item.createdAt)
            binding.tvTime.text = formatTime(item.createdAt)
            binding.tvAddress.text = item.block ?: "Unknown Address"

            binding.root.setOnClickListener {
                onItemClick(item)
            }
        }

        private fun formatDate(isoString: String?): String {
            return try {
                isoString?.let {
                    val odt = OffsetDateTime.parse(it)
                    odt.toLocalDate().format(DateTimeFormatter.ofPattern("dd MMM yyyy"))
                } ?: "Unknown Date"
            } catch (e: Exception) {
                "Unknown Date"
            }
        }

        private fun formatTime(isoString: String?): String {
            return try {
                isoString?.let {
                    val odt = OffsetDateTime.parse(it)
                    odt.toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm"))
                } ?: "Unknown Time"
            } catch (e: Exception) {
                "Unknown Time"
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<DataItemSecurityByUser>() {
            override fun areItemsTheSame(oldItem: DataItemSecurityByUser, newItem: DataItemSecurityByUser): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: DataItemSecurityByUser, newItem: DataItemSecurityByUser): Boolean {
                return oldItem == newItem
            }
        }
    }
}