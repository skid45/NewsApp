package com.skid.saved

import com.skid.news.model.Article

interface SavedRouter {

    fun onArticleProfile(article: Article)
}