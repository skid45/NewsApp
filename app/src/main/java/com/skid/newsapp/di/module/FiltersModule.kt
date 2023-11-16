package com.skid.newsapp.di.module

import com.skid.database.sources.dao.FiltersDao
import com.skid.database.sources.database.NewsAppDatabase
import com.skid.filters.repository.FiltersRepository
import com.skid.filters.repository.FiltersRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
interface FiltersModule {

    @[Binds Singleton]
    fun bindFiltersRepository(impl: FiltersRepositoryImpl): FiltersRepository

    companion object {

        @[Provides Singleton]
        fun provideFiltersDao(database: NewsAppDatabase): FiltersDao {
            return database.getFiltersDao()
        }
    }

}