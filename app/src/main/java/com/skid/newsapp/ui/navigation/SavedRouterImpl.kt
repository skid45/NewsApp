package com.skid.newsapp.ui.navigation

import com.github.terrakok.cicerone.Router
import com.skid.news.model.Article
import com.skid.saved.SavedRouter
import javax.inject.Inject

class SavedRouterImpl @Inject constructor(
    private val router: Router,
) : SavedRouter {

    override fun onArticleProfile(article: Article) {
        router.navigateTo(Screens.ArticleProfileScreen(article))
    }
}