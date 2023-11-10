package com.skid.filters.repository

import com.skid.filters.model.Language
import com.skid.filters.model.Sorting
import kotlinx.coroutines.flow.Flow
import java.util.Calendar

interface FiltersRepository {

    fun getSortBy(): Flow<Sorting>
    fun getChosenDates(): Flow<Pair<Calendar, Calendar>?>
    fun getLanguages(): Flow<Language?>
    fun getNumberOfFilters(): Flow<Int>

    suspend fun saveFilters(
        sortBy: Sorting,
        chosenDates: Pair<Calendar, Calendar>?,
        language: Language?,
        numberOfFilters: Int,
    )
}