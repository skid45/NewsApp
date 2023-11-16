package com.skid.news.repository

import com.skid.news.model.Article
import kotlinx.coroutines.flow.Flow

interface SavedArticlesRepository {

    fun isExists(url: String): Flow<Boolean>

    suspend fun saveArticle(article: Article)

    suspend fun deleteArticleByUrl(url: String)

    suspend fun getAllArticles(): List<Article>

    suspend fun deleteOldArticles(timestampInMillis: Long)
}