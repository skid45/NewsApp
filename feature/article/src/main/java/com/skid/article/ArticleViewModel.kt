package com.skid.article

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.skid.news.model.Article
import com.skid.news.repository.SavedArticlesRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ArticleViewModel @AssistedInject constructor(
    private val savedArticlesRepository: SavedArticlesRepository,
    @Assisted private val url: String,
) : ViewModel() {

    val isArticleSaved = savedArticlesRepository
        .isExists(url)
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

    companion object {

        @Suppress("UNCHECKED_CAST")
        fun viewModelFactory(
            assistedFactory: Factory,
            url: String,
        ): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return assistedFactory.create(url) as T
                }
            }
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(url: String): ArticleViewModel
    }
}