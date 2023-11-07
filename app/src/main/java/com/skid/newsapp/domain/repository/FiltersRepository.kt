package com.skid.newsapp.domain.repository

import com.skid.newsapp.domain.model.Language
import com.skid.newsapp.domain.model.Sorting
import kotlinx.coroutines.flow.Flow
import java.util.Calendar

interface FiltersRepository {

    fun getSortBy(): Flow<Sorting>
    fun getChosenDates(): Flow<Pair<Calendar, Calendar>?>
    fun getLanguages(): Flow<List<Language>>
    fun getNumberOfFilters(): Flow<Int>

    suspend fun saveFilters(
        sortBy: Sorting,
        chosenDates: Pair<Calendar, Calendar>?,
        languages: List<Language>,
        numberOfFilters: Int,
    )
}