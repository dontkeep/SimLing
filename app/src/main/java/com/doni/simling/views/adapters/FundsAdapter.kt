package com.doni.simling.views.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.doni.simling.databinding.ItemFundBinding
import com.doni.simling.models.connections.responses.DataItemFunds
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.Locale

class FundsAdapter: PagingDataAdapter<DataItemFunds, FundsAdapter.ViewHolder>(DIFF_CALLBACK) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
       val binding = ItemFundBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }

    inner class ViewHolder(private val binding: ItemFundBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(fundItem: DataItemFunds) {

            binding.tvBayar.text = fundItem.amount.toString()
            binding.tvDate.text = formatDate(fundItem.createdAt)
            binding.tvDesc.text = fundItem.userName.toString()
        }

        private fun formatDate(dateString: String?): String {
            if (dateString.isNullOrBlank()) return "-"
            return try {
                val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                val formatter = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
                val date = parser.parse(dateString)
                if (date != null) formatter.format(date) else "-"
            } catch (e: Exception) {
                "-"
            }
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<DataItemFunds>() {
            override fun areItemsTheSame(oldItem: DataItemFunds, newItem: DataItemFunds): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: DataItemFunds, newItem: DataItemFunds): Boolean {
                return oldItem == newItem
            }
        }
    }
}