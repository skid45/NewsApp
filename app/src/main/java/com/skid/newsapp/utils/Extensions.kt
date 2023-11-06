package com.skid.newsapp.utils

import android.content.Context
import android.util.TypedValue
import androidx.annotation.AttrRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
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

fun <T : Flow<R>, R> Fragment.collectFlow(flow: T, collectBlock: (R) -> Unit) {
    viewLifecycleOwner.lifecycleScope.launch {
        viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            flow.collect(collectBlock)
        }
    }
}

fun Calendar.format(pattern: String, locale: Locale = Locale.getDefault()): String {
    return SimpleDateFormat(pattern, locale).format(this.time)
}

fun Pair<Calendar, Calendar>.getDisplayChosenRange(): String {
    return when {
        first == second -> first.format("MMM dd, yyyy")
        first[Calendar.YEAR] == second[Calendar.YEAR] -> {
            val firstDate = first.format("MMM dd")
            val secondDate = second.format("MMM dd, yyyy")
            "$firstDate - $secondDate"
        }
        else -> {
            val firstDate = first.format("MMM dd, yyyy")
            val secondDate = second.format("MMM dd, yyyy")
            "$firstDate - $secondDate"
        }
    }
}