package com.skid.newsapp.data.remote.mapper

import com.skid.newsapp.data.remote.model.SourceDTO
import com.skid.newsapp.domain.model.Source
import com.skid.newsapp.utils.getCountryName

fun SourceDTO.toSource(): Source {
    return Source(
        id = id,
        name = name,
        category = category.replaceFirstChar { it.uppercase() },
        country = country.getCountryName()
    )
}