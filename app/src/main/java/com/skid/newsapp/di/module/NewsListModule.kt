package com.skid.newsapp.di.module

import com.skid.network.service.EverythingService
import com.skid.news.repository.NewsRepository
import com.skid.news.repository.NewsRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import javax.inject.Singleton

@Module(includes = [NewsListBySourceModule::class])
interface NewsListModule {

    @[Binds Singleton]
    fun bindNewsRepository(impl: NewsRepositoryImpl): NewsRepository

    companion object {

        @[Provides Singleton]
        fun provideNewsService(retrofit: Retrofit): EverythingService {
            return retrofit.create(EverythingService::class.java)
        }
    }
}