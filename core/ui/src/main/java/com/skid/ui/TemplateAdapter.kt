package com.skid.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil.ItemCallback
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

class TemplateAdapter<T, V : ViewBinding>(
    private val layoutInflater: (parent: ViewGroup) -> LayoutInflater,
    private val binding: (layoutInflater: LayoutInflater, parent: ViewGroup) -> V,
    private val bind: V.(item: T) -> Unit,
    diffCallback: ItemCallback<T>,
) : ListAdapter<T, TemplateViewHolder<V>>(diffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TemplateViewHolder<V> {
        val binding = binding(layoutInflater(parent), parent)
        return TemplateViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TemplateViewHolder<V>, position: Int) {
        val item = getItem(position)
        holder.binding.bind(item)
    }
}

class TemplateViewHolder<V : ViewBinding>(
    val binding: V,
) : RecyclerView.ViewHolder(binding.root)

fun <T, V : ViewBinding> createAdapter(
    layoutInflater: (parent: ViewGroup) -> LayoutInflater = { LayoutInflater.from(it.context) },
    binding: (layoutInflater: LayoutInflater, parent: ViewGroup) -> V,
    bind: V.(item: T) -> Unit,
    diffCallback: ItemCallback<T>,
) = TemplateAdapter(
    layoutInflater = layoutInflater,
    binding = binding,
    bind = bind,
    diffCallback = diffCallback
)