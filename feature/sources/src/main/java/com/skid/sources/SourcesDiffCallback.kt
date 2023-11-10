package com.skid.sources

import androidx.recyclerview.widget.DiffUtil
import com.skid.sources.model.Source

class SourcesDiffCallback : DiffUtil.ItemCallback<Source>() {
    override fun areItemsTheSame(oldItem: Source, newItem: Source): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Source, newItem: Source): Boolean {
        return oldItem == newItem
    }
}