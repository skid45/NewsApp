package com.skid.news.mapper

import com.skid.database.sources.model.SourceEntity
import com.skid.network.model.SourceDTO
import com.skid.utils.getCountryName

fun SourceDTO.toSourceEntity(): SourceEntity {
    return SourceEntity(
        id = id,
        name = name,
        category = category.replaceFirstChar { it.uppercase() },
        country = country.getCountryName(),
        language = language
    )
}