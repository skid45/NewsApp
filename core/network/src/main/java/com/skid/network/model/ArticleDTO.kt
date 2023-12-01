package com.skid.network.model

data class ArticleDTO(
    val content: String,
    val description: String?,
    val publishedAt: String,
    val source: SourceForArticleDTO,
    val title: String,
    val url: String,
    val urlToImage: String?
)