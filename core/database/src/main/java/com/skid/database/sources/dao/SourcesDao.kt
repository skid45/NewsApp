package com.skid.database.sources.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.skid.database.sources.model.SourceEntity
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

@Dao
interface SourcesDao {

    @Query("SELECT * FROM sources")
    suspend fun getAllSources(): List<SourceEntity>

    @Query("SELECT * FROM sources WHERE name LIKE '%' || :query || '%'")
    suspend fun getSourcesByQuery(query: String): List<SourceEntity>

    @Query("SELECT * FROM sources WHERE language = :language")
    suspend fun getSourcesByLanguage(language: String): List<SourceEntity>

    @Query("SELECT * FROM sources WHERE (category = :category) AND " +
            "(:language IS NULL OR language = :language)")
    fun getSourcesByCategory(category: String, language: String?): Single<List<SourceEntity>>

    @Query("SELECT * FROM sources WHERE name = :name")
    suspend fun getSourceByName(name: String): SourceEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllSources(sources: List<SourceEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllSourcesCompletable(sources: List<SourceEntity>): Completable

    @Query("DELETE FROM sources")
    suspend fun deleteAll()
}