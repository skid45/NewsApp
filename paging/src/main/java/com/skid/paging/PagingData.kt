package com.skid.paging

sealed class PagingData<Value : Any>(
    val data: List<Value>? = null,
    val error: String? = null,
    val isFirst: Boolean = false,
    val isLast: Boolean = false,
) {
    class Page<Value : Any>(
        data: List<Value>,
        isFirst: Boolean,
        isLast: Boolean,
    ) : PagingData<Value>(data = data, isFirst = isFirst, isLast = isLast)

    class Error<Value : Any>(
        error: String?,
    ) : PagingData<Value>(error = error, isLast = true)
}