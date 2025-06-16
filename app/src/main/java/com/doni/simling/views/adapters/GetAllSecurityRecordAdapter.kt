package com.doni.simling.views.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.doni.simling.databinding.ItemPresenceBinding
import com.doni.simling.models.connections.responses.DataItem3
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

class GetAllSecurityRecordAdapter(
    private val onItemClick: (DataItem3) -> Unit
) : PagingDataAdapter<DataItem3, GetAllSecurityRecordAdapter.ViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemPresenceBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    inner class ViewHolder(private val binding: ItemPresenceBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: DataItem3?) {
            item?.let {
                binding.tvUser.text = it.securityName ?: "Unknown User"
                binding.tvDate.text = formatDate(it.createdAt)
                binding.tvTime.text = formatTime(it.createdAt)
                binding.tvAddress.text = it.block ?: "Unknown Address"

                binding.root.setOnClickListener {
                    onItemClick(item)
                }
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
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<DataItem3>() {
            override fun areItemsTheSame(oldItem: DataItem3, newItem: DataItem3): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: DataItem3, newItem: DataItem3): Boolean {
                return oldItem == newItem
            }
        }
    }
}