package com.skid.database.sources.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.skid.database.sources.model.CachedArticleEntity
import io.reactivex.rxjava3.core.Single

@Dao
interface CachedArticlesDao {

    @Query(
        "SELECT * FROM cached_articles WHERE category = :category AND " +
                "(:from IS NULL OR published_at >= :from) AND " +
                "(:to IS NULL OR published_at <= :to) AND " +
                "(source_name IN (:sourceNames)) " +
                "ORDER BY published_at DESC " +
                "LIMIT :pageSize OFFSET :pageNumber * :pageSize"
    )
    fun getArticlesPageByCategory(
        pageSize: Int,
        pageNumber: Int,
        category: String,
        from: Long? = null,
        to: Long? = null,
        sourceNames: List<String>,
    ): Single<List<CachedArticleEntity>>

    @Query(
        "SELECT * FROM cached_articles WHERE " +
                "title LIKE '%' || :query || '%' OR " +
                "description LIKE '%' || :query || '%' OR " +
                "content LIKE '%' || :query || '%' " +
                "ORDER BY published_at DESC " +
                "LIMIT :pageSize OFFSET :pageNumber * :pageSize"
    )
    fun getArticlesPageByQuery(
        pageSize: Int,
        pageNumber: Int,
        query: String,
    ): Single<List<CachedArticleEntity>>

    @Query("SELECT CASE WHEN COUNT(*) > 0 THEN 0 ELSE 1 END FROM cached_articles")
    fun isCacheEmpty(): Single<Boolean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertArticles(articles: List<CachedArticleEntity>)

    @Query("DELETE FROM cached_articles WHERE category = :category AND (source_name IN (:sourceNames))")
    fun deleteAllByCategory(category: String, sourceNames: List<String>)

}