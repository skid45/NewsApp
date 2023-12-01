package com.skid.newsapp.ui.navigation

import com.github.terrakok.cicerone.Router
import com.skid.headlines.HeadlinesRouter
import com.skid.news.model.Article
import javax.inject.Inject

class HeadlinesRouterImpl @Inject constructor(
    private val router: Router
) : HeadlinesRouter {

    override fun onArticleProfile(article: Article) {
        router.navigateTo(Screens.ArticleProfileScreen(article))
    }

    override fun onError(message: String) {
        router.navigateTo(Screens.ErrorScreen(message))
    }
}