package com.skid.sources.usecase

import com.skid.sources.model.Source
import com.skid.sources.repository.SourcesRepository
import javax.inject.Inject

class GetSourcesByQueryUseCase @Inject constructor(
    private val sourcesRepository: SourcesRepository,
) {

    suspend operator fun invoke(query: String): Result<List<Source>> {
        return if (query.isBlank()) Result.success(emptyList())
        else sourcesRepository.getSources(refresh = false, query = query)
    }
}