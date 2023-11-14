package com.skid.newsapp.di.module

import com.skid.database.sources.dao.SourcesDao
import com.skid.database.sources.database.NewsAppDatabase
import com.skid.network.service.SourcesService
import com.skid.newsapp.ui.navigation.SourcesRouterImpl
import com.skid.sources.SourcesRouter
import com.skid.sources.repository.SourcesRepository
import com.skid.sources.repository.SourcesRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
interface SourcesModule {

    @[Binds Singleton]
    fun bindSourcesRepository(sourcesRepositoryImpl: SourcesRepositoryImpl): SourcesRepository

    @[Binds Singleton]
    fun bindSourcesRouter(sourcesRouterImpl: SourcesRouterImpl): SourcesRouter

    companion object {

        @[Provides Singleton]
        fun provideSourcesService(retrofit: Retrofit): SourcesService {
            return retrofit.create(SourcesService::class.java)
        }

        @[Provides Singleton]
        fun provideSourcesDao(database: NewsAppDatabase): SourcesDao = database.getSourcesDao()
    }
}