package com.skid.database.sources.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Calendar

@Entity(tableName = "filters")
data class FiltersEntity(
    @PrimaryKey val id: Int = 1,
    @ColumnInfo(name = "sort_by") val sortBy: String? = null,
    @ColumnInfo(name = "from") val from: Calendar? = null,
    @ColumnInfo(name = "to") val to: Calendar? = null,
    @ColumnInfo(name = "language") val language: String? = null,
    @ColumnInfo(name = "number_of_filters") val numberOfFilters: Int? = 0
)
