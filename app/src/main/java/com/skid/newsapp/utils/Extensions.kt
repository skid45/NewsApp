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