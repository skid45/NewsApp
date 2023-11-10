package com.skid.sources.repository

import com.skid.sources.model.Source


interface SourcesRepository {

    suspend fun getSources(language: String? = null): Result<List<Source>>
}