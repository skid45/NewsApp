package com.skid.news.model

import androidx.annotation.DrawableRes
import java.util.Calendar

data class Article(
    val url: String,
    val title: String,
    val description: String,
    val content: String,
    val publishedAt: Calendar,
    val imageUrl: String,
    val sourceName: String,
    @DrawableRes val sourceDrawableId: Int
)
