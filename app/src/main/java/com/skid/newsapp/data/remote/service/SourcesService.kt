package com.skid.newsapp.data.remote.service

import com.skid.newsapp.data.remote.model.SourcesResponse
import retrofit2.Response
import retrofit2.http.GET

interface SourcesService {

    @GET("top-headlines/sources")
    suspend fun getSources(): Response<SourcesResponse>
}