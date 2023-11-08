package com.skid.newsapp.di.module

import com.skid.sources.repository.SourcesRepositoryImpl
import com.skid.network.service.SourcesService
import com.skid.sources.repository.SourcesRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
interface SourcesNetworkModule {

    @[Binds Singleton]
    fun bindSourcesRepository(sourcesRepositoryImpl: SourcesRepositoryImpl): SourcesRepository

    companion object {

        @[Provides Singleton]
        fun provideSourcesService(retrofit: Retrofit): SourcesService {
            return retrofit.create(SourcesService::class.java)
        }
    }
}