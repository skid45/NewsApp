package com.skid.newsapp.di.module

import com.skid.database.sources.dao.CachedArticlesDao
import com.skid.database.sources.database.NewsAppDatabase
import com.skid.headlines.HeadlinesRouter
import com.skid.newsapp.ui.navigation.HeadlinesRouterImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
interface HeadlinesModule {

    @[Binds Singleton]
    fun bindHeadlinesRouter(impl: HeadlinesRouterImpl): HeadlinesRouter

    companion object {

        @[Provides Singleton]
        fun provideCachedArticlesDao(database: NewsAppDatabase): CachedArticlesDao {
            return database.getCachedArticlesDao()
        }
    }

}