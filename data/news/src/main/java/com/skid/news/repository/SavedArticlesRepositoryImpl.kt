package com.skid.news.repository

import com.skid.database.sources.dao.SavedArticlesDao
import com.skid.database.sources.model.SavedArticleEntity
import com.skid.news.mapper.toArticle
import com.skid.news.mapper.toSavedArticleEntity
import com.skid.news.model.Article
import kotlinx.coroutines.flow.Flow
import java.util.Calendar
import javax.inject.Inject

class SavedArticlesRepositoryImpl @Inject constructor(
    private val savedArticlesDao: SavedArticlesDao,
) : SavedArticlesRepository {

    override fun isExists(url: String): Flow<Boolean> {
        return savedArticlesDao.isExists(url)
    }

    override suspend fun saveArticle(article: Article) {
        savedArticlesDao.insert(article.toSavedArticleEntity())
    }

    override suspend fun deleteArticleByUrl(url: String) {
        savedArticlesDao.deleteByUrl(url)
    }

    override suspend fun getAllArticles(chosenDates: Pair<Calendar, Calendar>?): List<Article> {
        return savedArticlesDao.getAllArticles(
            chosenDates?.first?.timeInMillis,
            chosenDates?.second?.timeInMillis
        ).map(SavedArticleEntity::toArticle)
    }

    override suspend fun getArticlesByQuery(query: String): List<Article> {
        return savedArticlesDao.getArticlesByQuery(query).map(SavedArticleEntity::toArticle)
    }

    override suspend fun deleteOldArticles(timestampInMillis: Long) {
        savedArticlesDao.deleteOldArticles(timestampInMillis)
    }
}