package com.skid.newsapp.di

import android.content.Context
import com.skid.article.di.ArticleDeps
import com.skid.filters.di.FiltersDeps
import com.skid.filters.repository.FiltersRepository
import com.skid.news.repository.NewsRepository
import com.skid.news.repository.SavedArticlesRepository
import com.skid.newsapp.di.module.DatabaseModule
import com.skid.newsapp.di.module.FiltersModule
import com.skid.newsapp.di.module.NavigationModule
import com.skid.newsapp.di.module.NetworkModule
import com.skid.newsapp.di.module.NewsListModule
import com.skid.newsapp.di.module.ResourceModule
import com.skid.newsapp.di.module.SavedModule
import com.skid.newsapp.di.module.SourcesModule
import com.skid.newsapp.ui.MainActivity
import com.skid.newsapp.ui.SplashScreenActivity
import com.skid.newslistbysource.NewsListBySourceRouter
import com.skid.newslistbysource.di.NewsListBySourceDeps
import com.skid.saved.SavedRouter
import com.skid.saved.di.SavedDeps
import com.skid.sources.SourcesRouter
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
        DatabaseModule::class,
        FiltersModule::class,
        SourcesModule::class,
        NewsListModule::class,
        SavedModule::class,
    ]
)
interface AppComponent : SourcesDeps, FiltersDeps, NewsListBySourceDeps, ArticleDeps, SavedDeps {

    override val filtersRepository: FiltersRepository

    override val sourcesRepository: SourcesRepository

    override val newsRepository: NewsRepository

    override val sourcesRouter: SourcesRouter

    override val newsListBySourceRouter: NewsListBySourceRouter

    override val savedArticlesRepository: SavedArticlesRepository

    override val savedRouter: SavedRouter

    fun inject(activity: MainActivity)

    fun inject(activity: SplashScreenActivity)

    @Component.Factory
    interface Factory {
        fun create(
            @BindsInstance context: Context,
        ): AppComponent
    }
}