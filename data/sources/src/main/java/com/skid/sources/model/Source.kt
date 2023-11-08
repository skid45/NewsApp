package com.skid.sources.model

import androidx.annotation.DrawableRes

data class Source(
    val id: String,
    val name: String,
    val category: String,
    val country: String,
    @DrawableRes val drawableResId: Int,
)
