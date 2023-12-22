package com.skid.news.repository

import com.skid.news.model.Article
import kotlinx.coroutines.flow.Flow
import java.util.Calendar

interface SavedArticlesRepository {

    fun isExists(url: String): Flow<Boolean>

    suspend fun saveArticle(article: Article)

    suspend fun deleteArticleByUrl(url: String)

    suspend fun getAllArticles(chosenDates: Pair<Calendar, Calendar>?, language: String?): List<Article>

    suspend fun getArticlesByQuery(query: String): List<Article>

    suspend fun deleteOldArticles(timestampInMillis: Long)
}