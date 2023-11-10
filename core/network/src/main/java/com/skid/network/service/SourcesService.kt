package com.skid.network.service

import com.skid.network.model.SourcesResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface SourcesService {

    @GET("top-headlines/sources")
    suspend fun getSources(
        @Query("language") language: String?
    ): Response<SourcesResponse>
}