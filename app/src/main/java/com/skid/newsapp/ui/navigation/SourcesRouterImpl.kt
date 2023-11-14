package com.skid.newsapp.ui.navigation

import com.github.terrakok.cicerone.Router
import com.skid.sources.SourcesRouter
import javax.inject.Inject

class SourcesRouterImpl @Inject constructor(
    private val router: Router,
) : SourcesRouter {

    override fun navigateToNewsListBySource(sourceId: String, sourceName: String) {
        router.navigateTo(Screens.NewsListScreenBySource(sourceId, sourceName))
    }
}