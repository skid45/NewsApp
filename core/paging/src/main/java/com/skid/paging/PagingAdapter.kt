package com.skid.paging

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
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
    private val diffCallback: DiffUtil.ItemCallback<T>,
    private val binding: (layoutInflater: LayoutInflater, parent: ViewGroup) -> V,
    private val bind: V.(item: T) -> Unit,
    private val layoutInflater: (parent: ViewGroup) -> LayoutInflater = { LayoutInflater.from(it.context) },
    private val doOnError: (message: String) -> Unit = {},
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var onLoadMoreListener: (() -> Unit)? = null

    private var dataset = emptyList<PagingAdapterItem<T>>()
        set(value) {
            val diffUtilCallback = PagingDiffUtil(field, value, diffCallback)
            val diffResult = DiffUtil.calculateDiff(diffUtilCallback)
            field = value
            diffResult.dispatchUpdatesTo(this)
        }

    override fun getItemViewType(position: Int): Int {
        return when (dataset[position]) {
            is PagingAdapterItem.Item -> ITEM_VIEW_TYPE
            is PagingAdapterItem.Loading -> LOADING_VIEW_TYPE
        }
    }

    override fun getItemCount(): Int = dataset.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == ITEM_VIEW_TYPE) {
            ItemViewHolder(binding(layoutInflater(parent), parent))
        } else {
            LoadingViewHolder(LoadingItemBinding.inflate(layoutInflater(parent), parent, false))
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = dataset[position]) {
            is PagingAdapterItem.Item -> (holder as ItemViewHolder<V>).binding.bind(item.item)
            is PagingAdapterItem.Loading -> {
                onLoadMoreListener?.invoke()
            }
        }
    }

    fun addOnLoadMoreListener(listener: () -> Unit) {
        onLoadMoreListener = listener
    }

    private fun submitList(list: MutableList<PagingAdapterItem<T>>, isNewList: Boolean) {
        dataset =
            if (isNewList) list
            else {
                val mutableDataset = dataset.toMutableList()
                if (dataset.isNotEmpty() && dataset.last() is PagingAdapterItem.Loading) {
                    mutableDataset.removeLast()
                }
                mutableDataset.addAll(list)
                mutableDataset
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


    private class PagingDiffUtil<T : Any>(
        private val oldList: List<PagingAdapterItem<T>>,
        private val newList: List<PagingAdapterItem<T>>,
        private val itemDiffCallback: DiffUtil.ItemCallback<T>,
    ) : DiffUtil.Callback() {
        override fun getOldListSize(): Int = oldList.size

        override fun getNewListSize(): Int = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldItem = oldList[oldItemPosition]
            val newItem = newList[newItemPosition]
            return if (oldItem is PagingAdapterItem.Item && newItem is PagingAdapterItem.Item) {
                itemDiffCallback.areItemsTheSame(oldItem.item, newItem.item)
            } else false
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldItem = oldList[oldItemPosition]
            val newItem = newList[newItemPosition]
            return if (oldItem is PagingAdapterItem.Item && newItem is PagingAdapterItem.Item) {
                itemDiffCallback.areContentsTheSame(oldItem.item, newItem.item)
            } else false
        }
    }
}