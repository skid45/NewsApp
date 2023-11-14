package com.skid.paging

import io.reactivex.rxjava3.core.Single

abstract class PagingSource<Value: Any> {

    abstract fun loadPage(pageSize: Int, pageNumber: Int): Single<LoadResult<Value>>
}
