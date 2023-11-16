package com.skid.filters.mapper

import com.skid.database.sources.model.FiltersEntity
import com.skid.filters.model.Filters
import com.skid.filters.model.Language
import com.skid.filters.model.Sorting

fun FiltersEntity?.toFilters(): Filters {
    return if (this == null) {
        Filters(
            sortBy = Sorting.NEW,
            chosenDates = null,
            language = null,
            numberOfFilters = 0
        )
    } else {
        Filters(
            sortBy = if (sortBy == null) Sorting.NEW else Sorting.valueOf(sortBy!!),
            chosenDates = if (from == null || to == null) null else from!! to to!!,
            language = if (language == null) null else Language.valueOf(language!!),
            numberOfFilters = numberOfFilters ?: 0
        )
    }
}

fun Filters.toFiltersEntity(): FiltersEntity {
    return FiltersEntity(
        sortBy = sortBy.name,
        from = chosenDates?.first,
        to = chosenDates?.second,
        language = language?.name,
        numberOfFilters = numberOfFilters
    )
}