package com.skid.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FiltersDataStorePreferencesManager @Inject constructor(
    private val filtersDataStore: DataStore<Preferences>,
) {

    fun getSortBy(): Flow<String?> = filtersDataStore
        .data
        .map { it[FiltersPreferencesKeys.SORT_BY] }


    fun getChosenDates(): Flow<Pair<Long, Long>?> = filtersDataStore
        .data
        .map { filters ->
            val startDate = filters[FiltersPreferencesKeys.START_DATE] ?: return@map null
            val endDate = filters[FiltersPreferencesKeys.END_DATE] ?: return@map null
            if (startDate == Long.MIN_VALUE) null
            else startDate to endDate
        }

    fun getLanguages(): Flow<String?> = filtersDataStore
        .data
        .map { filters ->
            val language = filters[FiltersPreferencesKeys.LANGUAGE]
            if (language == "null") null
            else language
        }

    fun getNumberOfFilters(): Flow<Int?> = filtersDataStore
        .data
        .map { it[FiltersPreferencesKeys.NUMBER_OF_FILTERS] }

    suspend fun saveFilters(
        sortBy: String,
        chosenDates: Pair<Long, Long>?,
        language: String?,
        numberOfFilters: Int,
    ) {
        filtersDataStore.edit { filters ->
            filters[FiltersPreferencesKeys.SORT_BY] = sortBy
            filters[FiltersPreferencesKeys.START_DATE] = chosenDates?.first ?: Long.MIN_VALUE
            filters[FiltersPreferencesKeys.END_DATE] = chosenDates?.second ?: Long.MIN_VALUE
            filters[FiltersPreferencesKeys.LANGUAGE] = language.toString()
            filters[FiltersPreferencesKeys.NUMBER_OF_FILTERS] = numberOfFilters
        }
    }
}