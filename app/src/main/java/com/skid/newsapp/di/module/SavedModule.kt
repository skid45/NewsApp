package com.skid.newsapp.di.module

import com.skid.database.sources.dao.SavedArticlesDao
import com.skid.database.sources.database.NewsAppDatabase
import com.skid.news.repository.SavedArticlesRepository
import com.skid.news.repository.SavedArticlesRepositoryImpl
import com.skid.newsapp.ui.navigation.SavedRouterImpl
import com.skid.saved.SavedRouter
import dagger.Binds
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
interface SavedModule {

    @[Binds Singleton]
    fun bindSavedArticlesRepository(impl: SavedArticlesRepositoryImpl): SavedArticlesRepository

    @[Binds Singleton]
    fun bindSavedRouter(impl: SavedRouterImpl): SavedRouter


    companion object {

        @[Provides Singleton]
        fun provideSavedArticlesDao(database: NewsAppDatabase): SavedArticlesDao {
            return database.getSavedArticlesDao()
        }
    }
}