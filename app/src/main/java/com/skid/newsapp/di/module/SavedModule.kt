package com.skid.newsapp.di.module

import com.skid.database.sources.dao.SavedArticlesDao
import com.skid.database.sources.database.NewsAppDatabase
import com.skid.news.repository.SavedArticlesRepository
import com.skid.news.repository.SavedArticlesRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
interface SavedModule {

    @[Binds Singleton]
    fun bindSavedArticlesRepository(impl: SavedArticlesRepositoryImpl): SavedArticlesRepository

    companion object {

        @[Provides Singleton]
        fun provideSavedArticlesDao(database: NewsAppDatabase): SavedArticlesDao {
            return database.getSavedArticlesDao()
        }
    }
}