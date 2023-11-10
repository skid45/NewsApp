package com.skid.filters.repository

import com.skid.datastore.FiltersDataStorePreferencesManager
import com.skid.filters.model.Language
import com.skid.filters.model.Sorting
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Calendar
import java.util.TimeZone
import javax.inject.Inject

class FiltersRepositoryImpl @Inject constructor(
    private val filtersDataStorePreferencesManager: FiltersDataStorePreferencesManager,
) : FiltersRepository {

    override fun getSortBy(): Flow<Sorting> = filtersDataStorePreferencesManager
        .getSortBy()
        .map { Sorting.valueOf(it ?: Sorting.NEW.name) }

    override fun getChosenDates(): Flow<Pair<Calendar, Calendar>?> =
        filtersDataStorePreferencesManager
            .getChosenDates()
            .map { chosenDate ->
                chosenDate ?: return@map null
                Pair(
                    first = Calendar.getInstance(TimeZone.getDefault()).apply {
                        timeInMillis = chosenDate.first
                    },
                    second = Calendar.getInstance(TimeZone.getDefault()).apply {
                        timeInMillis = chosenDate.second
                    }
                )
            }

    override fun getLanguage(): Flow<Language?> = filtersDataStorePreferencesManager
        .getLanguages()
        .map { language ->
            language ?: return@map null
            Language.valueOf(language) }

    override fun getNumberOfFilters(): Flow<Int> = filtersDataStorePreferencesManager
        .getNumberOfFilters()
        .map { it ?: 0 }

    override suspend fun saveFilters(
        sortBy: Sorting,
        chosenDates: Pair<Calendar, Calendar>?,
        language: Language?,
        numberOfFilters: Int,
    ) {
        filtersDataStorePreferencesManager.saveFilters(
            sortBy = sortBy.name,
            chosenDates = if (chosenDates != null) {
                chosenDates.first.timeInMillis to chosenDates.second.timeInMillis
            } else null,
            language = language?.name,
            numberOfFilters = numberOfFilters
        )
    }
}