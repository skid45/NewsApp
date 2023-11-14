package com.skid.paging

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers

class Pager<Value : Any>(
    private val pageSize: Int,
    private val initialPage: Int = 0,
    private val pagingSourceFactory: () -> PagingSource<Value>,
) {

    private var nextPage: Int = initialPage

    fun loadNextPage() = pagingSourceFactory()
        .loadPage(pageSize, nextPage)
        .flatMapObservable { loadResult ->
            when (loadResult) {
                is LoadResult.Page -> {
                    nextPage++
                    Observable.just(
                        PagingData.Page(
                            data = loadResult.data,
                            isFirst = nextPage - 1 == initialPage,
                            isLast = loadResult.data.size < pageSize || loadResult.data.isEmpty()
                        )
                    )
                }

                is LoadResult.Error -> {
                    Observable.just(PagingData.Error(loadResult.e.localizedMessage))
                }
            }
        }
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
}

