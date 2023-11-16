package com.skid.filters.repository

import com.skid.filters.model.Filters
import kotlinx.coroutines.flow.Flow

interface FiltersRepository {

    fun getFilters(): Flow<Filters>

    suspend fun saveFilters(filters: Filters)
}