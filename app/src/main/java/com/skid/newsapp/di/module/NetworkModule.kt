package com.skid.newsapp.di.module

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.skid.newsapp.BuildConfig
import com.skid.network.utils.AuthInterceptor
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module(includes = [SourcesNetworkModule::class])
object NetworkModule {

    @Provides
    fun provideBaseUrl() = "https://newsapi.org/v2/"

    @[Provides Singleton]
    fun provideGson(): Gson {
        return GsonBuilder()
            .create()
    }


    @[Provides Singleton]
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(BuildConfig.API_KEY))
            .build()
    }


    @[Provides Singleton]
    fun provideRetrofit(baseUrl: String, gson: Gson, client: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(client)
            .build()
    }

}
