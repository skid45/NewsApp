package com.skid.sources.repository


import com.skid.database.sources.dao.SourcesDao
import com.skid.database.sources.model.SourceEntity
import com.skid.network.model.SourceDTO
import com.skid.network.service.SourcesService
import com.skid.sources.mapper.toSource
import com.skid.sources.mapper.toSourceEntity
import com.skid.ui.R
import com.skid.sources.model.Source
import com.skid.utils.ResourceWrapper
import java.io.IOException
import javax.inject.Inject

class SourcesRepositoryImpl @Inject constructor(
    private val sourcesService: SourcesService,
    private val sourcesDao: SourcesDao,
    private val resourceWrapper: ResourceWrapper,
) : SourcesRepository {

    override suspend fun getSources(
        refresh: Boolean,
        language: String?,
        query: String?,
    ): Result<List<Source>> {
        if (!refresh) {
            val sources = getSourcesFromDB(language, query)
            return if (sources.isEmpty()) getSources(true, language, query)
            else Result.success(sources)
        }

        val result = try {
            val response = sourcesService.getSources()
            if (response.isSuccessful) {
                Result.success(response.body()!!.sources.map(SourceDTO::toSourceEntity))
            } else {
                Result.failure(Exception(resourceWrapper.getString(R.string.something_went_wrong_try_later)))
            }
        } catch (e: IOException) {
            Result.failure(Exception(resourceWrapper.getString(R.string.no_internet_connection)))
        } catch (e: Exception) {
            Result.failure(Exception(resourceWrapper.getString(R.string.something_went_wrong_try_later)))
        }

        return if (result.isSuccess) {
            sourcesDao.deleteAll()
            sourcesDao.insertAllSources(result.getOrThrow())
            Result.success(getSourcesFromDB(language, query))
        } else {
            val sources = getSourcesFromDB(language, query)
            if (sources.isNotEmpty()) {
                Result.success(sources)
            } else {
                Result.failure(result.exceptionOrNull()!!)
            }
        }
    }

    private suspend fun getSourcesFromDB(language: String?, query: String?): List<Source> {
        return when {
            language != null -> sourcesDao.getSourcesByLanguage(language)
            query != null -> sourcesDao.getSourcesByQuery(query)
            else -> sourcesDao.getAllSources()
        }.map(SourceEntity::toSource)
    }
}