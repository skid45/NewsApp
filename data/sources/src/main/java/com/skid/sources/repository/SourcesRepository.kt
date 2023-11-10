package com.skid.sources.repository

import com.skid.sources.model.Source


interface SourcesRepository {

    suspend fun getSources(
        refresh: Boolean = true,
        language: String? = null,
        query: String? = null,
    ): Result<List<Source>>
}