package com.skid.newslistbysource.di

import com.skid.filters.repository.FiltersRepository
import com.skid.news.repository.NewsRepository
import com.skid.newslistbysource.NewsListBySourceFragment
import dagger.Component
import kotlin.properties.Delegates.notNull

@[NewsListBySourceScope Component(dependencies = [NewsListBySourceDeps::class])]
interface NewsListBySourceComponent {

    fun inject(fragment: NewsListBySourceFragment)

    @Component.Factory
    interface Factory {

        fun create(deps: NewsListBySourceDeps): NewsListBySourceComponent
    }
}

interface NewsListBySourceDeps {

    val newsRepository: NewsRepository
    val filtersRepository: FiltersRepository
}

interface NewsListBySourceDepsProvider {

    val deps: NewsListBySourceDeps

    companion object : NewsListBySourceDepsProvider by NewsListBySourceDepsStore
}

object NewsListBySourceDepsStore : NewsListBySourceDepsProvider {
    override var deps: NewsListBySourceDeps by notNull()
}
