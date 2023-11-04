package com.skid.newsapp.data.remote.repository

import com.skid.newsapp.R
import com.skid.newsapp.data.remote.mapper.toSource
import com.skid.newsapp.data.remote.model.SourceDTO
import com.skid.newsapp.data.remote.service.SourcesService
import com.skid.newsapp.domain.model.Source
import com.skid.newsapp.domain.repository.SourcesRepository
import com.skid.newsapp.utils.ResourceWrapper
import java.io.IOException
import javax.inject.Inject

class SourcesRepositoryImpl @Inject constructor(
    private val sourcesService: SourcesService,
    private val resourceWrapper: ResourceWrapper,
) : SourcesRepository {

    override suspend fun getSources(): Result<List<Source>> {
        return try {
            val response = sourcesService.getSources()
            if (response.isSuccessful) {
                Result.success(response.body()!!.sources.map(SourceDTO::toSource))
            } else {
                Result.failure(Exception(resourceWrapper.getString(R.string.something_went_wrong_try_later)))
            }
        } catch (e: IOException) {
            Result.failure(Exception(resourceWrapper.getString(R.string.no_internet_connection)))
        } catch (e: Exception) {
            Result.failure(Exception(resourceWrapper.getString(R.string.something_went_wrong_try_later)))
        }
    }
}