package com.skid.database.sources.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.skid.database.sources.dao.SourcesDao
import com.skid.database.sources.model.SourceEntity

@Database(entities = [SourceEntity::class], version = 1, exportSchema = false)
abstract class NewsAppDatabase : RoomDatabase() {
    abstract fun getSourcesDao(): SourcesDao
}