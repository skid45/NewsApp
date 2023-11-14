package com.skid.news.mapper

import com.skid.network.model.ArticleDTO
import com.skid.news.model.Article
import com.skid.ui.R
import com.skid.ui.sourcesDrawablesMap
import com.skid.utils.parseToCalendar

fun ArticleDTO.toArticle(): Article {
    return Article(
        url = url,
        title = title,
        description = description,
        content = content,
        publishedAt = publishedAt.parseToCalendar("yyyy-MM-dd'T'HH:mm:ss'Z'"),
        imageUrl = urlToImage,
        sourceName = source.name,
        sourceDrawableId = sourcesDrawablesMap[source.name] ?: R.drawable.source_photo_stub
    )
}
