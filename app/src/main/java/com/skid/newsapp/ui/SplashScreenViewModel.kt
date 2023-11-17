package com.skid.newsapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.skid.news.repository.SavedArticlesRepository
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Provider

class SplashScreenViewModel @Inject constructor(
    private val savedArticlesRepository: SavedArticlesRepository,
) : ViewModel() {

    init {
        viewModelScope.launch {
            val twoWeeksAgoInMillis = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(14)
            savedArticlesRepository.deleteOldArticles(twoWeeksAgoInMillis)
        }
    }


    @Suppress("UNCHECKED_CAST")
    class Factory @Inject constructor(
        private val viewModelProvider: Provider<SplashScreenViewModel>,
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            require(modelClass == SplashScreenViewModel::class.java)
            return viewModelProvider.get() as T
        }
    }
}