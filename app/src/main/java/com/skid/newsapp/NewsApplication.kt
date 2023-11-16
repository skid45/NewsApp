package com.skid.newsapp

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.skid.article.di.ArticleDepsStore
import com.skid.filters.di.FiltersDepsStore
import com.skid.newsapp.di.AppComponent
import com.skid.newsapp.di.DaggerAppComponent
import com.skid.newslistbysource.di.NewsListBySourceDepsStore
import com.skid.saved.di.SavedDepsStore
import com.skid.sources.di.SourcesDepsStore
import com.skid.utils.Constants.FILTERS_PREFERENCES

class NewsApplication : Application() {

    private var _appComponent: AppComponent? = null
    val appComponent: AppComponent get() = checkNotNull(_appComponent)

    override fun onCreate() {
        super.onCreate()
        _appComponent = DaggerAppComponent.factory().create(this)
        SourcesDepsStore.deps = appComponent
        FiltersDepsStore.deps = appComponent
        NewsListBySourceDepsStore.deps = appComponent
        ArticleDepsStore.deps = appComponent
        SavedDepsStore.deps = appComponent
    }
}

val Context.appComponent: AppComponent
    get() = when (this) {
        is NewsApplication -> appComponent
        else -> (applicationContext as NewsApplication).appComponent
    }

val Context.filtersDataStore: DataStore<Preferences> by preferencesDataStore(name = FILTERS_PREFERENCES)