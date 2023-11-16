package com.skid.filters.repository

import com.skid.database.sources.dao.FiltersDao
import com.skid.database.sources.model.FiltersEntity
import com.skid.filters.mapper.toFilters
import com.skid.filters.mapper.toFiltersEntity
import com.skid.filters.model.Filters
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class FiltersRepositoryImpl @Inject constructor(
    private val filtersDao: FiltersDao,
) : FiltersRepository {

    override fun getFilters(): Flow<Filters> {
        return filtersDao.getFilters().map(FiltersEntity?::toFilters)
    }

    override suspend fun saveFilters(filters: Filters) {
        filtersDao.saveFilters(filters.toFiltersEntity())
    }
}