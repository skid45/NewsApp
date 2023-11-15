package com.skid.news.usecase

import com.skid.news.pagingsource.NewsBySourcePagingSource
import com.skid.news.repository.NewsRepository
import javax.inject.Inject

class GetNewsBySourcePagingSourceWithQueryUseCase @Inject constructor(
    private val newsRepository: NewsRepository,
) {

    operator fun invoke(query: String?, source: String?): NewsBySourcePagingSource {
        return if (query.isNullOrBlank()) {
            newsRepository.newsBySourcePagingSource(
                query = null,
                source = null,
                sortBy = null,
                from = null,
                to = null,
                language = null
            )
        } else {
            newsRepository.newsBySourcePagingSource(
                query,
                source,
                null,
                null,
                null,
                null
            )
        }
    }
}