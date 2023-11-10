package com.skid.newsapp.di

import android.content.Context
import com.skid.filters.di.FiltersDeps
import com.skid.filters.repository.FiltersRepository
import com.skid.newsapp.di.module.FiltersModule
import com.skid.newsapp.di.module.NavigationModule
import com.skid.newsapp.di.module.NetworkModule
import com.skid.newsapp.di.module.ResourceModule
import com.skid.newsapp.di.module.SourcesModule
import com.skid.newsapp.ui.MainActivity
import com.skid.sources.di.SourcesDeps
import com.skid.sources.repository.SourcesRepository
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        NavigationModule::class,
        ResourceModule::class,
        NetworkModule::class,
        FiltersModule::class,
        SourcesModule::class
    ]
)
interface AppComponent : SourcesDeps, FiltersDeps {

    override val filtersRepository: FiltersRepository

    override val sourcesRepository: SourcesRepository

    fun inject(activity: MainActivity)

    @Component.Factory
    interface Factory {
        fun create(
            @BindsInstance context: Context,
        ): AppComponent
    }
}