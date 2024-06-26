package com.skid.sources.di

import com.skid.filters.repository.FiltersRepository
import com.skid.sources.SourcesFragment
import com.skid.sources.SourcesRouter
import com.skid.sources.repository.SourcesRepository
import dagger.Component
import kotlin.properties.Delegates.notNull

@[SourcesScope Component(dependencies = [SourcesDeps::class])]
internal interface SourcesComponent {

    fun inject(fragment: SourcesFragment)

    @Component.Factory
    interface Factory {

        fun create(deps: SourcesDeps): SourcesComponent
    }
}

interface SourcesDeps {

    val sourcesRepository: SourcesRepository
    val filtersRepository: FiltersRepository
    val sourcesRouter: SourcesRouter
}

interface SourcesDepsProvider {

    val deps: SourcesDeps

    companion object : SourcesDepsProvider by SourcesDepsStore
}

object SourcesDepsStore : SourcesDepsProvider {
    override var deps: SourcesDeps by notNull()
}
