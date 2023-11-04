package com.skid.newsapp.utils

import android.content.Context
import androidx.annotation.StringRes

class ResourceWrapper(private val context: Context) {

    fun getString(@StringRes id: Int): String {
        return context.getString(id)
    }
}