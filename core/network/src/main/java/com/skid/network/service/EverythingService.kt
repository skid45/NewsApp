package com.skid.network.service

import androidx.annotation.IntRange
import com.skid.network.model.EverythingResponse
import io.reactivex.rxjava3.core.Single
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface EverythingService {

    @GET("everything")
    fun getNewsBySource(
        @Query("sources") source: String? = null,
        @Query("page") @IntRange(from = 1) page: Int = 1,
        @Query("pageSize") @IntRange(1, MAX_PAGE_SIZE.toLong()) pageSize: Int = MAX_PAGE_SIZE,
        @Query("sortBy") sortBy: String? = null,
        @Query("from") from: String? = null,
        @Query("to") to: String? = null,
        @Query("language") language: String? = null,
    ): Single<Response<EverythingResponse>>
}

private const val MAX_PAGE_SIZE = 100