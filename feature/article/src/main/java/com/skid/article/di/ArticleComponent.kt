package com.skid.article.di

import com.skid.article.ArticleFragment
import com.skid.news.repository.SavedArticlesRepository
import dagger.Component
import kotlin.properties.Delegates.notNull

@[ArticleScope Component(dependencies = [ArticleDeps::class])]
internal interface ArticleComponent {

    fun inject(fragment: ArticleFragment)

    @Component.Factory
    interface Factory {

        fun create(deps: ArticleDeps): ArticleComponent
    }
}

interface ArticleDeps {

    val savedArticlesRepository: SavedArticlesRepository
}

interface ArticleDepsProvider {

    val deps: ArticleDeps

    companion object : ArticleDepsProvider by ArticleDepsStore
}

object ArticleDepsStore : ArticleDepsProvider {

    override var deps: ArticleDeps by notNull()
}
