package com.skid.newsapp.di.module

import android.content.Context
import com.skid.newsapp.utils.ResourceWrapper
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
object ResourceModule {

    @[Provides Singleton]
    fun provideResourceWrapper(context: Context) = ResourceWrapper(context)
}