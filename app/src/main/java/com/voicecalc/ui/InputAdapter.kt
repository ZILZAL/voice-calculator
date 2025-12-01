package com.voicecalc.ui

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.voicecalc.databinding.ItemInputBinding
import com.voicecalc.model.InputItem

class InputAdapter(
    private val onItemUpdated: (String, String) -> Unit,
    private val onItemDeleted: (String) -> Unit
) : ListAdapter<InputItem, InputAdapter.InputViewHolder>(InputDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InputViewHolder {
        val binding = ItemInputBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return InputViewHolder(binding)
    }

    override fun onBindViewHolder(holder: InputViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class InputViewHolder(
        private val binding: ItemInputBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        private var currentItem: InputItem? = null
        private var textWatcher: TextWatcher? = null

        fun bind(item: InputItem) {
            currentItem = item

            // Remove old text watcher
            textWatcher?.let { binding.etValue.removeTextChangedListener(it) }

            // Set value
            if (binding.etValue.text.toString() != item.value) {
                binding.etValue.setText(item.value)
            }

            // Setup text watcher
            textWatcher = object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    val newValue = s.toString()
                    if (newValue != item.value) {
                        onItemUpdated(item.id, newValue)
                    }
                }
            }
            binding.etValue.addTextChangedListener(textWatcher)

            // Delete button
            binding.btnDelete.setOnClickListener {
                onItemDeleted(item.id)
            }

            // Change background color if edited
            if (item.isEdited) {
                binding.root.setBackgroundColor(0xFFFFF9C4.toInt()) // Light yellow
            } else {
                binding.root.setBackgroundColor(0xFFFFFFFF.toInt()) // White
            }
        }
    }

    private class InputDiffCallback : DiffUtil.ItemCallback<InputItem>() {
        override fun areItemsTheSame(oldItem: InputItem, newItem: InputItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: InputItem, newItem: InputItem): Boolean {
            return oldItem == newItem
        }
    }
}
