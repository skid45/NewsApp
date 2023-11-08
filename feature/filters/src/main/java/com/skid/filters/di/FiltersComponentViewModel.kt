package com.skid.filters.di

import androidx.lifecycle.ViewModel

internal class FiltersComponentViewModel : ViewModel() {

    val filtersComponent = DaggerFiltersComponent
        .factory()
        .create(deps = FiltersDepsProvider.deps)
}