package com.skid.newsapp.di

import com.skid.newsapp.di.module.NavigationModule
import com.skid.newsapp.ui.MainActivity
import dagger.Component
import javax.inject.Singleton

@[Singleton Component(modules = [NavigationModule::class])]
interface AppComponent {

    fun inject(activity: MainActivity)

    @Component.Factory
    interface Factory {
        fun create(): AppComponent
    }
}