package com.skid.sources.mapper

import com.skid.database.sources.model.SourceEntity
import com.skid.network.model.SourceDTO
import com.skid.sources.model.Source
import com.skid.ui.R
import com.skid.utils.getCountryName

fun SourceDTO.toSource(): Source {
    return Source(
        id = id,
        name = name,
        category = category.replaceFirstChar { it.uppercase() },
        country = country.getCountryName(),
        drawableResId = drawableMap[name] ?: R.drawable.source_photo_stub
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
        drawableResId = drawableMap[name] ?: R.drawable.source_photo_stub
    )
}

private val drawableMap = mapOf(
    "CNBC" to R.drawable.cnbc,
    "CNN" to R.drawable.cnn,
    "Daily Mail" to R.drawable.daily_mail,
    "BBC" to R.drawable.bbc,
    "FOX NEWS" to R.drawable.fox_news,
    "Bloomberg" to R.drawable.bloomberg,
    "The New York Times" to R.drawable.the_new_york_times
)