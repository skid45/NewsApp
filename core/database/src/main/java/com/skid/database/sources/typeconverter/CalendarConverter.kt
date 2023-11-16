package com.skid.database.sources.typeconverter

import androidx.room.TypeConverter
import java.util.Calendar
import java.util.TimeZone

class CalendarConverter {
    @TypeConverter
    fun fromTimestamp(value: Long): Calendar {
        return Calendar.getInstance(TimeZone.getDefault()).apply {
            timeInMillis = value
        }
    }

    @TypeConverter
    fun toTimestamp(timestamp: Calendar): Long {
        return timestamp.timeInMillis
    }
}