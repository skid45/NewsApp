package com.skid.newsapp.utils

import android.content.Context
import android.util.TypedValue
import androidx.annotation.AttrRes
import java.util.Locale

fun String.getCountryName(): String {
    return Locale("", this).displayCountry
}

fun Context.resolveAttributeColor(@AttrRes attrId: Int): Int {
    return TypedValue().apply {
        this@resolveAttributeColor
            .theme
            .resolveAttribute(attrId, this, true)
    }.data
}