package com.skid.headlines

import com.skid.news.model.Article

interface HeadlinesRouter {

    fun onArticleProfile(article: Article)

    fun onError(message: String)
}