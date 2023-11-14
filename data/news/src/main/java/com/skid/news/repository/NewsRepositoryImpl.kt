package com.skid.news.repository

import com.skid.news.pagingsource.NewsBySourcePagingSource
import javax.inject.Inject

class NewsRepositoryImpl @Inject constructor(
    private val newsBySourcePagingSourceFactory: NewsBySourcePagingSource.Factory,
) : NewsRepository {

    override fun newsBySourcePagingSource(
        source: String?,
        sortBy: String?,
        from: String?,
        to: String?,
        language: String?,
    ): NewsBySourcePagingSource {
        return newsBySourcePagingSourceFactory.create(source, sortBy, from, to, language)
    }
}