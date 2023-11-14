package com.skid.paging

sealed class LoadResult<Value : Any> {
    data class Page<Value : Any>(
        val data: List<Value>,
        val nextKey: Int
    ) : LoadResult<Value>()

    data class Error<Value : Any>(val e: Exception) : LoadResult<Value>()
}
