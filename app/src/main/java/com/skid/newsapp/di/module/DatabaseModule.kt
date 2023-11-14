package com.skid.newsapp.di.module

import android.content.Context
import androidx.room.Room
import com.skid.database.sources.database.NewsAppDatabase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
object DatabaseModule {

    @[Provides Singleton]
    fun provideSourcesDatabase(context: Context): NewsAppDatabase {
        return Room.databaseBuilder(context, NewsAppDatabase::class.java, "news_app_db")
            .fallbackToDestructiveMigration()
            .build()
    }
}