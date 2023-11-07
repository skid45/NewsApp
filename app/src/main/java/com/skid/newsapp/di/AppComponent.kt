package com.skid.newsapp.di

import android.content.Context
import com.skid.newsapp.di.module.FiltersModule
import com.skid.newsapp.di.module.NavigationModule
import com.skid.newsapp.di.module.NetworkModule
import com.skid.newsapp.di.module.ResourceModule
import com.skid.newsapp.ui.MainActivity
import com.skid.newsapp.ui.filters.FiltersFragment
import com.skid.newsapp.ui.sources.SourcesFragment
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        NavigationModule::class,
        ResourceModule::class,
        NetworkModule::class,
        FiltersModule::class
    ]
)
interface AppComponent {

    fun inject(activity: MainActivity)
    fun inject(fragment: SourcesFragment)
    fun inject(fragment: FiltersFragment)

    @Component.Factory
    interface Factory {
        fun create(
            @BindsInstance context: Context,
        ): AppComponent
    }
}