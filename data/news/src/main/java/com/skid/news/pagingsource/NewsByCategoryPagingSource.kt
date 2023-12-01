package com.skid.news.pagingsource

import com.skid.database.sources.dao.CachedArticlesDao
import com.skid.database.sources.dao.SourcesDao
import com.skid.database.sources.model.CachedArticleEntity
import com.skid.database.sources.model.SourceEntity
import com.skid.network.model.SourceDTO
import com.skid.network.service.EverythingService
import com.skid.network.service.SourcesService
import com.skid.news.mapper.toArticle
import com.skid.news.mapper.toCachedArticleEntity
import com.skid.news.mapper.toSourceEntity
import com.skid.news.model.Article
import com.skid.paging.LoadResult
import com.skid.paging.PagingSource
import com.skid.ui.R
import com.skid.utils.ResourceWrapper
import com.skid.utils.parseToCalendar
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import java.io.IOException
import java.util.concurrent.TimeUnit

class NewsByCategoryPagingSource @AssistedInject constructor(
    private val sourcesService: SourcesService,
    private val sourcesDao: SourcesDao,
    private val newsService: EverythingService,
    private val cachedArticlesDao: CachedArticlesDao,
    private val resourceWrapper: ResourceWrapper,
    @Assisted("initialPage") private val initialPage: Int,
    @Assisted("category") private val category: String,
    @Assisted("sortBy") private val sortBy: String?,
    @Assisted("from") private val from: String?,
    @Assisted("to") private val to: String?,
    @Assisted("language") private val language: String?,
) : PagingSource<Article>() {

    override fun loadPage(pageSize: Int, pageNumber: Int): Single<LoadResult<Article>> {
        val sourcesSingle = sourcesDao.getSourcesByCategory(category, language)
            .flatMap { sources ->
                if (sources.isEmpty()) {
                    sourcesService.getSourcesSingle()
                        .flatMap { response ->
                            if (response.isSuccessful) {
                                sourcesDao.insertAllSourcesCompletable(
                                    response.body()!!.sources.map(SourceDTO::toSourceEntity)
                                ).andThen(sourcesDao.getSourcesByCategory(category, language))
                            } else Single.just(emptyList())
                        }
                } else Single.just(sources)
            }
            .onErrorReturn { emptyList() }

        val response = sourcesSingle.flatMap { sources ->
            if (sources.isEmpty()) {
                return@flatMap Single
                    .just(LoadResult.Error(Exception(resourceWrapper.getString(R.string.something_went_wrong_try_later))))
            }
            newsService.getNews(
                source = sources.take(20).map(SourceEntity::id).joinToString(","),
                pageSize = pageSize,
                page = pageNumber,
                sortBy = sortBy,
                from = from,
                to = to
            ).map { response ->
                if (response.isSuccessful) {
                    LoadResult.Page(
                        data = response.body()!!.articles.map { articleDTO ->
                            articleDTO.toCachedArticleEntity(category)
                        },
                        nextKey = pageNumber + 1
                    )
                } else {
                    LoadResult.Error(Exception(resourceWrapper.getString(R.string.something_went_wrong_try_later)))
                }
            }.onErrorReturn { e ->
                when (e) {
                    is IOException -> LoadResult.Error(Exception(resourceWrapper.getString(R.string.no_internet_connection)))
                    else -> LoadResult.Error(Exception(resourceWrapper.getString(R.string.something_went_wrong_try_later)))
                }
            }
        }

        return response.flatMap { loadResult ->
            when (loadResult) {
                is LoadResult.Page -> {
                    if (pageNumber == initialPage) {
                        sourcesSingle
                            .doOnSuccess { sources ->
                                cachedArticlesDao.deleteAllByCategory(
                                    category = category,
                                    sourceNames = sources.map(SourceEntity::name)
                                )
                            }
                            .subscribe()
                    }
                    cachedArticlesDao.insertArticles(loadResult.data)
                    Single.just(
                        LoadResult.Page(
                            data = loadResult.data.map(CachedArticleEntity::toArticle),
                            nextKey = pageNumber + 1
                        )
                    )
                }

                is LoadResult.Error -> {
                    sourcesSingle.flatMap { sources ->
                        if (sources.isEmpty()) {
                            Single.just(LoadResult.Page(emptyList<Article>(), pageNumber + 1))
                        } else {
                            cachedArticlesDao
                                .getArticlesPageByCategory(
                                    pageSize = pageSize,
                                    pageNumber = pageNumber - initialPage,
                                    category = category,
                                    from = from?.parseToCalendar("yyyy MM dd")?.timeInMillis,
                                    to = to?.parseToCalendar("yyyy MM dd")
                                        ?.timeInMillis
                                        ?.plus(TimeUnit.DAYS.toMillis(1)),
                                    sourceNames = sources.map(SourceEntity::name)
                                )
                                .map { cachedArticles ->
                                    if (cachedArticles.isNotEmpty()) {
                                        LoadResult.Page(
                                            data = cachedArticles.map(CachedArticleEntity::toArticle),
                                            nextKey = pageNumber + 1
                                        )
                                    } else {
                                        LoadResult.Error(loadResult.e)
                                    }
                                }
                        }
                    }
                }
            }
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted("initialPage") initialPage: Int,
            @Assisted("category") category: String,
            @Assisted("sortBy") sortBy: String?,
            @Assisted("from") from: String?,
            @Assisted("to") to: String?,
            @Assisted("language") language: String?,
        ): NewsByCategoryPagingSource
    }
}