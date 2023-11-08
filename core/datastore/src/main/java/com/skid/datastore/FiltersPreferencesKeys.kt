package com.skid.datastore

import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey

object FiltersPreferencesKeys {
    val SORT_BY = stringPreferencesKey("sort_by")
    val START_DATE = longPreferencesKey("start_date")
    val END_DATE = longPreferencesKey("end_date")
    val LANGUAGES = stringSetPreferencesKey("languages")
    val NUMBER_OF_FILTERS = intPreferencesKey("number_of_filters")
}