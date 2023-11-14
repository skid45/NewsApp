package com.skid.paging

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.skid.paging.databinding.LoadingItemBinding

private const val ITEM_VIEW_TYPE = 0
private const val LOADING_VIEW_TYPE = 1

sealed class PagingAdapterItem<T> {
    data class Item<T>(val item: T) : PagingAdapterItem<T>()
    class Loading<T> : PagingAdapterItem<T>()
}

class PagingAdapter<T : Any, V : ViewBinding>(
    diffCallback: DiffUtil.ItemCallback<T>,
    private val binding: (layoutInflater: LayoutInflater, parent: ViewGroup) -> V,
    private val bind: V.(item: T) -> Unit,
    private val layoutInflater: (parent: ViewGroup) -> LayoutInflater = { LayoutInflater.from(it.context) },
    private val doOnError: (message: String) -> Unit = {},
) : ListAdapter<PagingAdapterItem<T>, RecyclerView.ViewHolder>(PagingDiffCallback(diffCallback)) {

    private var onLoadMoreListener: (() -> Unit)? = null

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is PagingAdapterItem.Item -> ITEM_VIEW_TYPE
            is PagingAdapterItem.Loading -> LOADING_VIEW_TYPE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == ITEM_VIEW_TYPE) {
            ItemViewHolder(binding(layoutInflater(parent), parent))
        } else {
            LoadingViewHolder(LoadingItemBinding.inflate(layoutInflater(parent), parent, false))
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = getItem(position)) {
            is PagingAdapterItem.Item -> (holder as ItemViewHolder<V>).binding.bind(item.item)
            is PagingAdapterItem.Loading -> {
                onLoadMoreListener?.invoke()
            }
        }
    }

    fun addOnLoadMoreListener(listener: () -> Unit) {
        onLoadMoreListener = listener
    }

    private fun submitList(list: MutableList<PagingAdapterItem<T>>?, isNewList: Boolean) {
        if (!isNewList) {
            val newList = currentList.toMutableList()
            if (currentList.isNotEmpty() && currentList.last() is PagingAdapterItem.Loading) {
                newList.removeLast()
            }
            if (list != null) {
                newList.addAll(list)
            }
            submitList(newList)
        } else {
            submitList(list)
        }
    }

    fun submitPage(pagingData: PagingData<T>) {
        when (pagingData) {
            is PagingData.Page -> {
                val mutablePage: MutableList<PagingAdapterItem<T>> = pagingData.data!!
                    .map { PagingAdapterItem.Item(it) }
                    .toMutableList()
                if (!pagingData.isLast) mutablePage.add(PagingAdapterItem.Loading())
                submitList(mutablePage, pagingData.isFirst)
            }

            is PagingData.Error -> {
                submitList(mutableListOf(), false)
                doOnError(pagingData.error.toString())
            }
        }
    }

    private class ItemViewHolder<V : ViewBinding>(
        val binding: V,
    ) : RecyclerView.ViewHolder(binding.root)

    private class LoadingViewHolder(
        binding: LoadingItemBinding,
    ) : RecyclerView.ViewHolder(binding.root)

    private class PagingDiffCallback<T : Any>(
        private val itemDiffCallback: DiffUtil.ItemCallback<T>,
    ) : DiffUtil.ItemCallback<PagingAdapterItem<T>>() {
        override fun areItemsTheSame(
            oldItem: PagingAdapterItem<T>,
            newItem: PagingAdapterItem<T>,
        ): Boolean {
            return if (oldItem is PagingAdapterItem.Item && newItem is PagingAdapterItem.Item) {
                itemDiffCallback.areItemsTheSame(oldItem.item, newItem.item)
            } else false
        }

        override fun areContentsTheSame(
            oldItem: PagingAdapterItem<T>,
            newItem: PagingAdapterItem<T>,
        ): Boolean {
            return if (oldItem is PagingAdapterItem.Item && newItem is PagingAdapterItem.Item) {
                itemDiffCallback.areContentsTheSame(oldItem.item, newItem.item)
            } else false
        }
    }
}