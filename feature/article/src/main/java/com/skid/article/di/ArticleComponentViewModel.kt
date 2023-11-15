package com.skid.article.di

import androidx.lifecycle.ViewModel

internal class ArticleComponentViewModel : ViewModel() {

    val articleComponent = DaggerArticleComponent.factory().create(deps = ArticleDepsProvider.deps)
}