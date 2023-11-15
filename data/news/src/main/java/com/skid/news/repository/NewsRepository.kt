package com.skid.news.repository

import com.skid.news.pagingsource.NewsBySourcePagingSource

interface NewsRepository {

    fun newsBySourcePagingSource(
        query: String?,
        source: String?,
        sortBy: String?,
        from: String?,
        to: String?,
        language: String?,
    ) : NewsBySourcePagingSource
}