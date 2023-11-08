package com.skid.sources.mapper

import com.skid.network.model.SourceDTO
import com.skid.sources.model.Source
import com.skid.utils.getCountryName

fun SourceDTO.toSource(): Source {
    return Source(
        id = id,
        name = name,
        category = category.replaceFirstChar { it.uppercase() },
        country = country.getCountryName()
    )
}