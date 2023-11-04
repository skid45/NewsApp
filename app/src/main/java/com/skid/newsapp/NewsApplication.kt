package com.skid.newsapp

import android.app.Application
import android.content.Context
import com.skid.newsapp.di.AppComponent
import com.skid.newsapp.di.DaggerAppComponent

class NewsApplication : Application() {

    private var _appComponent: AppComponent? = null
    val appComponent: AppComponent get() = checkNotNull(_appComponent)

    override fun onCreate() {
        super.onCreate()
        _appComponent = DaggerAppComponent.factory().create(this)
    }
}

val Context.appComponent: AppComponent
    get() = when (this) {
        is NewsApplication -> appComponent
        else -> (applicationContext as NewsApplication).appComponent
    }