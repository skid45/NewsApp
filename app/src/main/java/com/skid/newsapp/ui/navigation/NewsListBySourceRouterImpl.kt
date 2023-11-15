package com.skid.newsapp.ui.navigation

import com.github.terrakok.cicerone.Router
import com.skid.news.model.Article
import com.skid.newslistbysource.NewsListBySourceRouter
import javax.inject.Inject

class NewsListBySourceRouterImpl @Inject constructor(
    private val router: Router
) : NewsListBySourceRouter {

    override fun toArticleProfile(article: Article) {
        router.navigateTo(Screens.ArticleProfileScreen(article))
    }
}