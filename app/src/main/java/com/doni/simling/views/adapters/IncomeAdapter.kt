package com.doni.simling.views.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.doni.simling.databinding.ItemIncomeBinding
import com.doni.simling.helper.DateHelper.formatDate
import com.doni.simling.helper.formatCurrency
import com.doni.simling.models.connections.responses.DataItemFunds

class IncomeAdapter(
    private val onItemClick: (DataItemFunds) -> Unit
) : PagingDataAdapter<DataItemFunds, IncomeAdapter.ViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemIncomeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }

    inner class ViewHolder(private val binding: ItemIncomeBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(incomeItem: DataItemFunds) {
            binding.tvAddress.text = incomeItem.block
            binding.tvMonth.text = formatDate(incomeItem.createdAt)
            binding.tvBayar.text = formatCurrency(incomeItem.amount ?: 0)

            // Tambahkan click listener
            binding.root.setOnClickListener {
                incomeItem.id?.let { id ->
                    onItemClick(incomeItem)
                }
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