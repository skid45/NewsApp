package com.skid.newsapp.di.module

import com.skid.newsapp.data.remote.repository.SourcesRepositoryImpl
import com.skid.newsapp.data.remote.service.SourcesService
import com.skid.newsapp.domain.repository.SourcesRepository
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