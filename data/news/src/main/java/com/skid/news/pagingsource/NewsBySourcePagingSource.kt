package com.skid.news.pagingsource

import com.skid.network.model.ArticleDTO
import com.skid.network.service.EverythingService
import com.skid.news.mapper.toArticle
import com.skid.news.model.Article
import com.skid.paging.LoadResult
import com.skid.paging.PagingSource
import com.skid.ui.R
import com.skid.utils.ResourceWrapper
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import java.io.IOException

class NewsBySourcePagingSource @AssistedInject constructor(
    private val newsService: EverythingService,
    private val resourceWrapper: ResourceWrapper,
    @Assisted("source") private val source: String?,
    @Assisted("sortBy") private val sortBy: String?,
    @Assisted("from") private val from: String?,
    @Assisted("to") private val to: String?,
    @Assisted("language") private val language: String?,
) : PagingSource<Article>() {

    override fun loadPage(pageSize: Int, pageNumber: Int): Single<LoadResult<Article>> {
        return newsService.getNewsBySource(
            source = source,
            page = pageNumber,
            pageSize = pageSize,
            sortBy = sortBy,
            from = from,
            to = to,
            language = language
        )
            .map { response ->
                if (response.isSuccessful) {
                    LoadResult.Page(
                        data = response.body()!!.articles.map(ArticleDTO::toArticle),
                        nextKey = pageNumber + 1
                    )
                } else {
                    when (response.code()) {
                        426 -> {
                            LoadResult.Page<Article>(emptyList(), pageNumber + 1)
                        }

                        429 -> {
                            LoadResult.Error(Exception(resourceWrapper.getString(R.string.too_many_requests_try_later)))
                        }

                        else -> {
                            LoadResult.Error(Exception(resourceWrapper.getString(R.string.something_went_wrong_try_later)))
                        }
                    }
                }
            }
            .onErrorReturn { e ->
                when (e) {
                    is IOException -> LoadResult.Error(Exception(resourceWrapper.getString(R.string.no_internet_connection)))
                    else -> LoadResult.Error(Exception(resourceWrapper.getString(R.string.something_went_wrong_try_later)))
                }
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }


    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted("source") source: String?,
            @Assisted("sortBy") sortBy: String?,
            @Assisted("from") from: String?,
            @Assisted("to") to: String?,
            @Assisted("language") language: String?,
        ): NewsBySourcePagingSource
    }
}