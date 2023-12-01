package com.skid.news.repository

import com.skid.news.pagingsource.NewsByCategoryPagingSource
import com.skid.news.pagingsource.NewsByQueryPagingSource
import com.skid.news.pagingsource.NewsBySourcePagingSource
import javax.inject.Inject

class NewsRepositoryImpl @Inject constructor(
    private val newsBySourcePagingSourceFactory: NewsBySourcePagingSource.Factory,
    private val newsByCategoryPagingSourceFactory: NewsByCategoryPagingSource.Factory,
    private val newsByQueryPagingSourceFactory: NewsByQueryPagingSource.Factory,
) : NewsRepository {

    override fun newsBySourcePagingSource(
        query: String?,
        source: String?,
        sortBy: String?,
        from: String?,
        to: String?,
        language: String?,
    ): NewsBySourcePagingSource {
        return newsBySourcePagingSourceFactory.create(query, source, sortBy, from, to, language)
    }

    override fun newsByCategoryPagingSource(
        initialPage: Int,
        category: String,
        sortBy: String?,
        from: String?,
        to: String?,
        language: String?,
    ): NewsByCategoryPagingSource {
        return newsByCategoryPagingSourceFactory
            .create(initialPage, category, sortBy, from, to, language)
    }

    override fun newsByQueryPagingSource(initialPage: Int, query: String): NewsByQueryPagingSource {
        return newsByQueryPagingSourceFactory.create(initialPage, query)
    }
}