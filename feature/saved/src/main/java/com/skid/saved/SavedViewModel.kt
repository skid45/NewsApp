package com.skid.saved

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.skid.news.model.Article
import com.skid.news.repository.SavedArticlesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider

class SavedViewModel @Inject constructor(
    private val savedArticlesRepository: SavedArticlesRepository,
) : ViewModel() {

    private val _savedArticles = MutableStateFlow(emptyList<Article>())
    val savedArticles = _savedArticles.asStateFlow()

    init {
        updateArticles()
    }

    fun updateArticles() {
        viewModelScope.launch {
            _savedArticles.value = savedArticlesRepository.getAllArticles()
        }
    }

    @Suppress("UNCHECKED_CAST")
    class Factory @Inject constructor(
        private val viewModelProvider: Provider<SavedViewModel>,
    ) : ViewModelProvider.Factory {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            require(modelClass == SavedViewModel::class.java)
            return viewModelProvider.get() as T
        }
    }
}