package com.skid.newsapp.di.module

import com.skid.newsapp.ui.navigation.NewsListBySourceRouterImpl
import com.skid.newslistbysource.NewsListBySourceRouter
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module
interface NewsListBySourceModule {

    @[Binds Singleton]
    fun bindNewsListBySource(impl: NewsListBySourceRouterImpl): NewsListBySourceRouter
}