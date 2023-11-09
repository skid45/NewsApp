package com.skid.sources.usecase

import com.skid.sources.model.Source
import com.skid.sources.repository.SourcesRepository
import javax.inject.Inject

class GetSourcesByQueryUseCase @Inject constructor(
    private val sourcesRepository: SourcesRepository,
) {

    suspend operator fun invoke(query: String): Result<List<Source>> {
        if (query.isBlank()) return Result.success(emptyList())

        val result = sourcesRepository.getSources()
        return if (result.isSuccess) {
            Result.success(result.getOrThrow().filter { source ->
                source.name.contains(query, ignoreCase = true)
            })
        } else result
    }
}