package com.skid.sources.repository


import com.skid.network.model.SourceDTO
import com.skid.network.service.SourcesService
import com.skid.sources.mapper.toSource
import com.skid.ui.R
import com.skid.sources.model.Source
import com.skid.utils.ResourceWrapper
import java.io.IOException
import javax.inject.Inject

class SourcesRepositoryImpl @Inject constructor(
    private val sourcesService: SourcesService,
    private val resourceWrapper: ResourceWrapper,
) : SourcesRepository {

    override suspend fun getSources(language: String?): Result<List<Source>> {
        return try {
            val response = sourcesService.getSources(language)
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