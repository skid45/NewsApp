package com.skid.database.sources.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.skid.database.sources.dao.SavedArticlesDao
import com.skid.database.sources.dao.SourcesDao
import com.skid.database.sources.model.SavedArticleEntity
import com.skid.database.sources.model.SourceEntity
import com.skid.database.sources.typeconverter.CalendarConverter

@Database(
    entities = [
        SourceEntity::class,
        SavedArticleEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(CalendarConverter::class)
abstract class NewsAppDatabase : RoomDatabase() {
    abstract fun getSourcesDao(): SourcesDao

    abstract fun getSavedArticlesDao(): SavedArticlesDao
}