package com.skid.filters.model

import java.util.Calendar

data class Filters(
    val sortBy: Sorting,
    val chosenDates: Pair<Calendar, Calendar>?,
    val language: Language?,
    val numberOfFilters: Int
)
