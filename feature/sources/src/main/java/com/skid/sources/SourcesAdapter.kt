package com.skid.sources

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.skid.sources.databinding.SourcesItemBinding
import com.skid.sources.model.Source

class SourcesAdapter(private val onItemClick: (Source) -> Unit) :
    ListAdapter<Source, SourcesAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = SourcesItemBinding.inflate(inflater, parent, false)
        return ViewHolder(binding, onItemClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(
        private val binding: SourcesItemBinding,
        private val onItemClick: (Source) -> Unit,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(source: Source) = with(binding) {
            root.setOnClickListener { onItemClick(source) }
            sourcesItemName.text = source.name
            sourcesItemCategory.text = source.category
            sourcesItemCountry.text = source.country
            sourcesItemImage.setImageResource(source.drawableResId)
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<Source>() {
        override fun areItemsTheSame(oldItem: Source, newItem: Source): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Source, newItem: Source): Boolean {
            return oldItem == newItem
        }
    }
}