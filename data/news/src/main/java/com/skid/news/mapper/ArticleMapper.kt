package com.skid.news.mapper

import com.skid.database.sources.model.CachedArticleEntity
import com.skid.database.sources.model.SavedArticleEntity
import com.skid.network.model.ArticleDTO
import com.skid.news.model.Article
import com.skid.ui.R
import com.skid.ui.sourcesDrawablesMap
import com.skid.utils.parseToCalendar

fun ArticleDTO.toArticle(): Article {
    return Article(
        url = url,
        title = title,
        description = description ?: "",
        content = content,
        publishedAt = publishedAt.parseToCalendar("yyyy-MM-dd'T'HH:mm:ss'Z'"),
        imageUrl = urlToImage,
        sourceName = source.name,
        sourceDrawableId = sourcesDrawablesMap[source.name] ?: R.drawable.source_photo_stub
    )
}

fun Article.toSavedArticleEntity(): SavedArticleEntity {
    return SavedArticleEntity(
        url = url,
        title = title,
        description = description,
        content = content,
        publishedAt = publishedAt,
        imageUrl = imageUrl,
        sourceName = sourceName,
        sourceDrawableId = sourceDrawableId
    )
}

fun SavedArticleEntity.toArticle(): Article {
    return Article(
        url = url,
        title = title,
        description = description,
        content = content,
        publishedAt = publishedAt,
        imageUrl = imageUrl,
        sourceName = sourceName,
        sourceDrawableId = sourceDrawableId
    )
}

fun ArticleDTO.toCachedArticleEntity(category: String): CachedArticleEntity {
    return CachedArticleEntity(
        url = url,
        title = title,
        description = description ?: "",
        content = content,
        publishedAt = publishedAt.parseToCalendar("yyyy-MM-dd'T'HH:mm:ss'Z'"),
        imageUrl = urlToImage,
        sourceName = source.name,
        sourceDrawableId = sourcesDrawablesMap[source.name] ?: R.drawable.source_photo_stub,
        category = category,
    )
}

fun CachedArticleEntity.toArticle(): Article {
    return Article(
        url = url,
        title = title,
        description = description,
        content = content,
        publishedAt = publishedAt,
        imageUrl = imageUrl,
        sourceName = sourceName,
        sourceDrawableId = sourceDrawableId
    )
}