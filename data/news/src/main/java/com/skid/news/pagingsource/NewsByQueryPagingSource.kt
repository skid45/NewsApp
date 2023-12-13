package com.skid.news.pagingsource

import com.skid.database.sources.dao.CachedArticlesDao
import com.skid.database.sources.model.CachedArticleEntity
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
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import java.io.IOException

class NewsByQueryPagingSource @AssistedInject constructor(
    private val newsService: EverythingService,
    private val cachedArticlesDao: CachedArticlesDao,
    private val resourceWrapper: ResourceWrapper,
    @Assisted("initialPage") private val initialPage: Int,
    @Assisted("query") private val query: String,
) : PagingSource<Article>() {

    override fun loadPage(pageSize: Int, pageNumber: Int): Single<LoadResult<Article>> {
        if (query.isBlank()) {
            return Single.just(LoadResult.Page(emptyList(), pageNumber + 1))
        }

        val networkResult = newsService
            .getNews(query = query, pageSize = pageSize, page = pageNumber)
            .map { response ->
                if (response.isSuccessful) {
                    LoadResult.Page(
                        data = response.body()!!.articles.map(ArticleDTO::toArticle),
                        nextKey = pageNumber + 1
                    )
                } else {
                    LoadResult.Error(Exception(resourceWrapper.getString(R.string.something_went_wrong_try_later)))
                }
            }
            .onErrorReturn { e ->
                when (e) {
                    is IOException -> LoadResult.Error(Exception(resourceWrapper.getString(R.string.no_internet_connection)))
                    else -> LoadResult.Error(Exception(resourceWrapper.getString(R.string.something_went_wrong_try_later)))
                }
            }

        return networkResult
            .flatMap { loadResult ->
                when (loadResult) {
                    is LoadResult.Page -> Single.just(loadResult)
                    is LoadResult.Error -> {
                        cachedArticlesDao
                            .isCacheEmpty()
                            .flatMap { isCacheEmpty ->
                                if (isCacheEmpty) {
                                    Single.just(loadResult)
                                } else {
                                    cachedArticlesDao
                                        .getArticlesPageByQuery(
                                            pageSize = pageSize,
                                            pageNumber = pageNumber - initialPage,
                                            query = query
                                        )
                                        .map { cachedArticleEntities ->
                                            LoadResult.Page(
                                                data = cachedArticleEntities.map(CachedArticleEntity::toArticle),
                                                nextKey = pageNumber + 1
                                            )
                                        }
                                }
                            }
                    }
                }
            }
            .subscribeOn(Schedulers.io())
    }

    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted("initialPage") initialPage: Int,
            @Assisted("query") query: String,
        ): NewsByQueryPagingSource
    }
}