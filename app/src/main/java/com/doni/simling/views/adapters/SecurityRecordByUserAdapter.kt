package com.doni.simling.views.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.doni.simling.models.connections.responses.DataItemSecurityByUser
import com.doni.simling.databinding.ItemPresenceBinding
import java.time.OffsetDateTime
import java.time.ZoneId
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

        private val jakartaZoneId = ZoneId.of("Asia/Jakarta")

        fun bind(item: DataItemSecurityByUser) {
            var date = formatDate(item.createdAt, jakartaZoneId) ?: "Unknown Date"
            var time = formatTime(item.createdAt, jakartaZoneId) ?: "Unknown Time"
            binding.tvUser.text = item.securityName ?: "Unknown User"
            binding.tvDate.text = date
            binding.tvTime.text = time

            binding.tvAddress.text = item.block ?: "Unknown Address"

            binding.root.setOnClickListener {
                onItemClick(item)
            }
        }

        private fun formatDate(isoString: String?, zoneId: ZoneId): String {
            Log.d("SecurityRecordByUserAdapter", "formatDate - ISO String: $isoString, Zone: $zoneId")
            return try {
                isoString?.let {
                    val odt = OffsetDateTime.parse(it)
                    val zdtInTargetZone = odt.atZoneSameInstant(zoneId)
                    zdtInTargetZone.toLocalDate().format(DateTimeFormatter.ofPattern("dd MMM yyyy"))
                } ?: "Unknown Date"
            } catch (e: Exception) {
                Log.e("SecurityRecordByUserAdapter", "Error formatting date: ${e.message}", e)
                "Unknown Date"
            }
        }

        private fun formatTime(isoString: String?, zoneId: ZoneId): String {
            Log.d("SecurityRecordByUserAdapter", "formatTime - ISO String: $isoString, Zone: $zoneId")
            return try {
                isoString?.let {
                    val odt = OffsetDateTime.parse(it)
                    val zdtInTargetZone = odt.atZoneSameInstant(zoneId)
                    zdtInTargetZone.toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm"))
                } ?: "Unknown Time"
            } catch (e: Exception) {
                Log.e("SecurityRecordByUserAdapter", "Error formatting time: ${e.message}", e)
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