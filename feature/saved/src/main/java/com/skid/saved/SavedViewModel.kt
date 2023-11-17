package com.skid.saved

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.skid.filters.repository.FiltersRepository
import com.skid.news.repository.SavedArticlesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject
import javax.inject.Provider

class SavedViewModel @Inject constructor(
    private val savedArticlesRepository: SavedArticlesRepository,
    filtersRepository: FiltersRepository,
) : ViewModel() {

    private val _refresh = MutableStateFlow(false)
    val refresh = _refresh.asStateFlow()

    val savedArticles = filtersRepository
        .getFilters()
        .combine(refresh) { filters, _ ->
            savedArticlesRepository.getAllArticles(filters.chosenDates)
        }
        .onEach { if (refresh.value) onRefreshChanged(false) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    fun onRefreshChanged(refresh: Boolean) {
        _refresh.value = refresh
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