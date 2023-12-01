package com.skid.news.repository

import com.skid.news.pagingsource.NewsByCategoryPagingSource
import com.skid.news.pagingsource.NewsByQueryPagingSource
import com.skid.news.pagingsource.NewsBySourcePagingSource

interface NewsRepository {

    fun newsBySourcePagingSource(
        query: String?,
        source: String?,
        sortBy: String?,
        from: String?,
        to: String?,
        language: String?,
    ): NewsBySourcePagingSource

    fun newsByCategoryPagingSource(
        initialPage: Int,
        category: String,
        sortBy: String?,
        from: String?,
        to: String?,
        language: String?,
    ): NewsByCategoryPagingSource

    fun newsByQueryPagingSource(
        initialPage: Int,
        query: String,
    ) : NewsByQueryPagingSource
}