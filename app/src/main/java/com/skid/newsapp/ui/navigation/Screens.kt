package com.skid.newsapp.ui.navigation

import androidx.core.os.bundleOf
import com.github.terrakok.cicerone.androidx.FragmentScreen
import com.skid.article.ArticleFragment
import com.skid.filters.FiltersFragment
import com.skid.news.model.Article
import com.skid.newslistbysource.NewsListBySourceFragment
import com.skid.saved.SavedFragment
import com.skid.sources.SourcesFragment
import com.skid.utils.Constants.ARTICLE_KEY
import com.skid.utils.Constants.SOURCE_ID_KEY
import com.skid.utils.Constants.SOURCE_NAME_KEY

object Screens {
    //    val HeadlinesScreen get() = FragmentScreen { HeadlinesFragment() }
    val SavedScreen get() = FragmentScreen { SavedFragment() }
    val SourcesScreen get() = FragmentScreen { SourcesFragment() }
    val FiltersScreen get() = FragmentScreen { FiltersFragment() }
    fun NewsListScreenBySource(sourceId: String, sourceName: String) = FragmentScreen {
        NewsListBySourceFragment().apply {
            arguments = bundleOf(
                SOURCE_ID_KEY to sourceId,
                SOURCE_NAME_KEY to sourceName
            )
        }
    }

    fun ArticleProfileScreen(article: Article) = FragmentScreen {
        ArticleFragment().apply {
            arguments = bundleOf(ARTICLE_KEY to article)
        }
    }
}