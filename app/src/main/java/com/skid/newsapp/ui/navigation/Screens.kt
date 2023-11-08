package com.skid.newsapp.ui.navigation

import com.github.terrakok.cicerone.androidx.FragmentScreen
import com.skid.filters.FiltersFragment
import com.skid.sources.SourcesFragment

object Screens {
//    val HeadlinesScreen get() = FragmentScreen { HeadlinesFragment() }
//    val SavedScreen get() = FragmentScreen { SavedFragment() }
    val SourcesScreen get() = FragmentScreen { SourcesFragment() }
    val FiltersScreen get() = FragmentScreen { FiltersFragment() }
}