package com.skid.database.sources.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.skid.database.sources.model.SavedArticleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SavedArticlesDao {

    @Query("SELECT EXISTS (SELECT 1 FROM saved_articles WHERE url = :url)")
    fun isExists(url: String): Flow<Boolean>

    @Upsert
    suspend fun insert(article: SavedArticleEntity)

    @Query("DELETE FROM saved_articles WHERE url = :url")
    suspend fun deleteByUrl(url: String)

    @Query("SELECT * FROM saved_articles WHERE " +
            "(:from IS NULL OR published_at >= :from) AND (:to IS NULL OR published_at <= :to)" +
            " ORDER BY published_at DESC")
    suspend fun getAllArticles(from: Long? = null, to: Long? = null): List<SavedArticleEntity>

    @Query("SELECT * FROM saved_articles WHERE " +
            "title LIKE '%' || :query || '%' OR " +
            "description LIKE '%' || :query || '%' OR " +
            "content LIKE '%' || :query || '%'")
    suspend fun getArticlesByQuery(query: String): List<SavedArticleEntity>

    @Query("DELETE FROM saved_articles WHERE created_at < :timestampInMillis")
    suspend fun deleteOldArticles(timestampInMillis: Long)
}