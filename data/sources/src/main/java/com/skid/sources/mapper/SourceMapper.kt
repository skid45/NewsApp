package com.skid.sources.mapper

import com.skid.database.sources.model.SourceEntity
import com.skid.network.model.SourceDTO
import com.skid.sources.model.Source
import com.skid.ui.R
import com.skid.ui.sourcesDrawablesMap
import com.skid.utils.getCountryName

fun SourceDTO.toSource(): Source {
    return Source(
        id = id,
        name = name,
        category = category.replaceFirstChar { it.uppercase() },
        country = country.getCountryName(),
        drawableResId = sourcesDrawablesMap[name] ?: R.drawable.source_photo_stub
    )
}


fun SourceDTO.toSourceEntity(): SourceEntity {
    return SourceEntity(
        id = id,
        name = name,
        category = category.replaceFirstChar { it.uppercase() },
        country = country.getCountryName(),
        language = language
    )
}

fun SourceEntity.toSource(): Source {
    return Source(
        id = id,
        name = name,
        category = category,
        country = country,
        drawableResId = sourcesDrawablesMap[name] ?: R.drawable.source_photo_stub
    )
}

