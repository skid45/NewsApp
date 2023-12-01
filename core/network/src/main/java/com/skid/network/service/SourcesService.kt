package com.skid.network.service

import com.skid.network.model.SourcesResponse
import io.reactivex.rxjava3.core.Single
import retrofit2.Response
import retrofit2.http.GET

interface SourcesService {

    @GET("top-headlines/sources")
    suspend fun getSources(): Response<SourcesResponse>

    @GET("top-headlines/sources")
    fun getSourcesSingle(): Single<Response<SourcesResponse>>
}