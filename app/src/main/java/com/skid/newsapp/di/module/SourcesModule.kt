package com.skid.newsapp.di.module

import android.content.Context
import androidx.room.Room
import com.skid.database.sources.dao.SourcesDao
import com.skid.database.sources.database.SourcesDatabase
import com.skid.network.service.SourcesService
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

    companion object {

        @[Provides Singleton]
        fun provideSourcesService(retrofit: Retrofit): SourcesService {
            return retrofit.create(SourcesService::class.java)
        }

        @[Provides Singleton]
        fun provideSourcesDatabase(context: Context): SourcesDatabase {
            return Room.databaseBuilder(context, SourcesDatabase::class.java, "sources_db")
                .fallbackToDestructiveMigration()
                .build()
        }

        @[Provides Singleton]
        fun provideSourcesDao(database: SourcesDatabase): SourcesDao = database.getSourcesDao()
    }
}