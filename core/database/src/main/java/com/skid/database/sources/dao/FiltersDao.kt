package com.skid.database.sources.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.skid.database.sources.model.FiltersEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FiltersDao {

    @Upsert
    suspend fun saveFilters(filters: FiltersEntity)

    @Query("SELECT * FROM filters WHERE id = 1")
    fun getFilters(): Flow<FiltersEntity?>
}