package com.skid.newsapp.ui.navigation

import androidx.fragment.app.FragmentManager
import com.github.terrakok.cicerone.Router
import com.skid.sources.SourcesRouter
import javax.inject.Inject

class SourcesRouterImpl @Inject constructor(
    private val router: Router,
) : SourcesRouter {

    override fun onNewsListBySource(sourceId: String, sourceName: String) {
        router.navigateTo(Screens.NewsListScreenBySource(sourceId, sourceName))
    }

    override fun onError(fragmentManager: FragmentManager, message: String) {
        router.navigateTo(Screens.ErrorScreen(message))
    }
}