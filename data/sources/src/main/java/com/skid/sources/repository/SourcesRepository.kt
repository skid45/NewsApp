package com.skid.sources.repository

import com.skid.sources.model.Source


interface SourcesRepository {

    suspend fun getSources(): Result<List<Source>>
}