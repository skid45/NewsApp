package com.skid.filters.di

import com.skid.filters.FiltersFragment
import com.skid.filters.repository.FiltersRepository
import dagger.Component
import kotlin.properties.Delegates.notNull

@[FiltersScope Component(dependencies = [FiltersDeps::class])]
internal interface FiltersComponent {

    fun inject(fragment: FiltersFragment)

    @Component.Factory
    interface Factory {

        fun create(deps: FiltersDeps): FiltersComponent
    }
}

interface FiltersDeps {
    val filtersRepository: FiltersRepository
}

interface FiltersDepsProvider {

    val deps: FiltersDeps

    companion object : FiltersDepsProvider by FiltersDepsStore
}

object FiltersDepsStore : FiltersDepsProvider {
    override var deps: FiltersDeps by notNull()
}
