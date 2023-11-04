package com.skid.newsapp.domain.repository

import com.skid.newsapp.domain.model.Source

interface SourcesRepository {

    suspend fun getSources(): Result<List<Source>>
}