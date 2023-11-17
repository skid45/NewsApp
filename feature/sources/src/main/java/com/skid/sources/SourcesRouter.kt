package com.skid.sources

import androidx.fragment.app.FragmentManager

interface SourcesRouter {

    fun onNewsListBySource(sourceId: String, sourceName: String)

    fun onError(fragmentManager: FragmentManager, message: String)
}