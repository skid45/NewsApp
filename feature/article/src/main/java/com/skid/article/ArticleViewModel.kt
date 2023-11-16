package com.skid.article

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.skid.news.model.Article
import com.skid.news.repository.SavedArticlesRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider

class ArticleViewModel @Inject constructor(
    private val savedArticlesRepository: SavedArticlesRepository,
) : ViewModel() {

    private val url = MutableStateFlow("")

    @OptIn(ExperimentalCoroutinesApi::class)
    val isArticleSaved = url
        .flatMapLatest(savedArticlesRepository::isExists)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = false
        )

    fun saveArticle(article: Article) {
        viewModelScope.launch {
            savedArticlesRepository.saveArticle(article)
        }
    }

    fun deleteArticle(url: String) {
        viewModelScope.launch {
            savedArticlesRepository.deleteArticleByUrl(url)
        }
    }

    fun onUrlChanged(url: String) {
        this.url.value = url
    }

    @Suppress("UNCHECKED_CAST")
    class Factory @Inject constructor(
        private val viewModelProvider: Provider<ArticleViewModel>,
    ) : ViewModelProvider.Factory {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            require(modelClass == ArticleViewModel::class.java)
            return viewModelProvider.get() as T
        }
    }
}