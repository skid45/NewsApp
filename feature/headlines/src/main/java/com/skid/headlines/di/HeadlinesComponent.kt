package com.skid.headlines.di

import com.skid.filters.repository.FiltersRepository
import com.skid.headlines.HeadlinesRouter
import com.skid.headlines.headlines.HeadlinesFragment
import com.skid.headlines.newsbycategory.NewsByCategoryFragment
import com.skid.news.repository.NewsRepository
import dagger.Component
import kotlin.properties.Delegates.notNull

@[HeadlinesScope Component(dependencies = [HeadlinesDeps::class])]
internal interface HeadlinesComponent {

    fun inject(fragment: HeadlinesFragment)

    fun inject(fragment: NewsByCategoryFragment)

    @Component.Factory
    interface Factory {

        fun create(deps: HeadlinesDeps): HeadlinesComponent
    }
}

interface HeadlinesDeps {

    val newsRepository: NewsRepository
    val filtersRepository: FiltersRepository
    val headlinesRouter: HeadlinesRouter
}

interface HeadlinesDepsProvider {

    val deps: HeadlinesDeps

    companion object : HeadlinesDepsProvider by HeadlinesDepsStore
}

object HeadlinesDepsStore : HeadlinesDepsProvider {

    override var deps: HeadlinesDeps by notNull()
}
