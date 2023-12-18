package com.skid.ui

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.TypedValue
import android.view.MenuItem
import android.view.WindowManager
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

fun Context.resolveAttributeColor(@AttrRes attrId: Int): Int {
    return TypedValue().apply {
        this@resolveAttributeColor
            .theme
            .resolveAttribute(attrId, this, true)
    }.data
}

fun Context.isDarkTheme(): Boolean {
    return resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK ==
            Configuration.UI_MODE_NIGHT_YES
}

fun Activity.updateStatusBarColor(@ColorInt color: Int) {
    this.window.apply {
        addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        statusBarColor = color
    }
}

fun Fragment.searchItemOnActionExpandListener(
    onExpand: ((item: MenuItem) -> Unit)? = null,
    onCollapse: ((item: MenuItem) -> Unit)? = null
) = object : MenuItem.OnActionExpandListener {
    override fun onMenuItemActionExpand(item: MenuItem): Boolean {
        if (requireContext().isDarkTheme()) {
            val surfaceContainerHighest = requireContext().resolveAttributeColor(
                com.google.android.material.R.attr.colorSurfaceContainerHighest
            )
            (requireActivity() as AppCompatActivity)
                .supportActionBar
                ?.setBackgroundDrawable(ColorDrawable(surfaceContainerHighest))
            requireActivity().updateStatusBarColor(surfaceContainerHighest)
        }
        onExpand?.invoke(item)
        return true
    }

    override fun onMenuItemActionCollapse(item: MenuItem): Boolean {
        onSearchItemCollapseInDarkTheme()
        onCollapse?.invoke(item)
        return true
    }
}

fun Fragment.onSearchItemCollapseInDarkTheme() {
    if (requireContext().isDarkTheme()) {
        (requireActivity() as AppCompatActivity)
            .supportActionBar
            ?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        requireActivity().updateStatusBarColor(Color.TRANSPARENT)
    }
}