package com.skid.newslistbysource

import com.skid.news.model.Article

interface NewsListBySourceRouter {

    fun toArticleProfile(article: Article)
}