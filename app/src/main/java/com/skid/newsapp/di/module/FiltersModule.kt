package com.skid.newsapp.di.module

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.skid.filters.repository.FiltersRepositoryImpl
import com.skid.filters.repository.FiltersRepository
import com.skid.newsapp.filtersDataStore
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
        fun provideFiltersDataStorePreferences(context: Context): DataStore<Preferences> {
            return context.filtersDataStore
        }
    }

}