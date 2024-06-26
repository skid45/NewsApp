package com.skid.database.sources.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Calendar

@Entity(tableName = "cached_articles")
data class CachedArticleEntity(
    @PrimaryKey val url: String,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "content") val content: String,
    @ColumnInfo(name = "published_at") val publishedAt: Calendar,
    @ColumnInfo(name = "image_url") val imageUrl: String?,
    @ColumnInfo(name = "source_name") val sourceName: String,
    @ColumnInfo(name = "source_drawable_id") val sourceDrawableId: Int,
    @ColumnInfo(name = "category") val category: String,
)
