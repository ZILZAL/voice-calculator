package com.voicecalc.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.voicecalc.databinding.ItemHistoryBinding
import com.voicecalc.model.CalculationResult

class HistoryAdapter : ListAdapter<CalculationResult, HistoryAdapter.HistoryViewHolder>(HistoryDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val binding = ItemHistoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return HistoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class HistoryViewHolder(
        private val binding: ItemHistoryBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(result: CalculationResult) {
            binding.tvHistoryItem.text = "• ${result}"
        }
    }

    private class HistoryDiffCallback : DiffUtil.ItemCallback<CalculationResult>() {
        override fun areItemsTheSame(oldItem: CalculationResult, newItem: CalculationResult): Boolean {
            return oldItem.timestamp == newItem.timestamp
        }

        override fun areContentsTheSame(oldItem: CalculationResult, newItem: CalculationResult): Boolean {
            return oldItem == newItem
        }
    }
}
