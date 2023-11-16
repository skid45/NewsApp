package com.skid.saved.di

import com.skid.filters.repository.FiltersRepository
import com.skid.news.repository.SavedArticlesRepository
import com.skid.saved.SavedFragment
import com.skid.saved.SavedRouter
import dagger.Component
import kotlin.properties.Delegates.notNull

@[SavedScope Component(dependencies = [SavedDeps::class])]
internal interface SavedComponent {

    fun inject(fragment: SavedFragment)

    @Component.Factory
    interface Factory {

        fun create(deps: SavedDeps): SavedComponent
    }
}

interface SavedDeps {

    val savedArticlesRepository: SavedArticlesRepository
    val filtersRepository: FiltersRepository
    val savedRouter: SavedRouter
}

interface SavedDepsProvider {

    val deps: SavedDeps

    companion object : SavedDepsProvider by SavedDepsStore
}

object SavedDepsStore : SavedDepsProvider {

    override var deps: SavedDeps by notNull()
}
