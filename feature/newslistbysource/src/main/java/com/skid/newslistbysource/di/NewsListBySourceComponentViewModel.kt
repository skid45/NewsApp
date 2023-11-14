package com.skid.newslistbysource.di

import androidx.lifecycle.ViewModel

class NewsListBySourceComponentViewModel : ViewModel() {
    val newsListBySourceComponent = DaggerNewsListBySourceComponent.factory()
        .create(deps = NewsListBySourceDepsProvider.deps)
}