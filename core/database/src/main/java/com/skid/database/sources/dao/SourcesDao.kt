package com.skid.database.sources.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.skid.database.sources.model.SourceEntity

@Dao
interface SourcesDao {

    @Query("SELECT * FROM sources")
    suspend fun getAllSources(): List<SourceEntity>

    @Query("SELECT * FROM sources WHERE name LIKE '%' || :query || '%'")
    suspend fun getSourcesByQuery(query: String) : List<SourceEntity>

    @Query("SELECT * FROM sources WHERE language = :language")
    suspend fun getSourcesByLanguage(language: String) : List<SourceEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllSources(sources: List<SourceEntity>)

    @Query("DELETE FROM sources")
    suspend fun deleteAll()
}