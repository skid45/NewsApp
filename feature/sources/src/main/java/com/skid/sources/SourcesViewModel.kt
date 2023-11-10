package com.skid.sources

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.skid.filters.repository.FiltersRepository
import com.skid.sources.model.Source
import com.skid.sources.repository.SourcesRepository
import com.skid.sources.usecase.GetSourcesByQueryUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider

class SourcesViewModel @Inject constructor(
    private val sourcesRepository: SourcesRepository,
    private val getSourcesByQueryUseCase: GetSourcesByQueryUseCase,
    private val filtersRepository: FiltersRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<SourcesUiState>(SourcesUiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        onEvent(SourcesEvent.OnUpdateSources)
    }

    fun onEvent(event: SourcesEvent) {
        when (event) {
            SourcesEvent.OnUpdateSources -> {
                _uiState.value = when (uiState.value) {
                    is SourcesUiState.Success -> SourcesUiState.Refresh
                    else -> SourcesUiState.Loading
                }
                updateSources()
            }

            is SourcesEvent.OnSearchByQuery -> searchByQuery(event.query)

        }
    }

    private fun updateSources() {
        viewModelScope.launch {
            filtersRepository.getLanguage().collect { language ->
                val result = sourcesRepository
                    .getSources(language = language?.apiName)
                _uiState.value =
                    if (result.isSuccess) SourcesUiState.Success(result.getOrThrow())
                    else SourcesUiState.Error(result.exceptionOrNull()?.localizedMessage ?: "")
            }
        }
    }

    private fun searchByQuery(query: String) {
        viewModelScope.launch {
            val result = getSourcesByQueryUseCase(query)
            _uiState.value =
                if (result.isSuccess) SourcesUiState.Search(result.getOrThrow())
                else SourcesUiState.Error(result.exceptionOrNull()?.localizedMessage ?: "")
        }
    }

    @Suppress("UNCHECKED_CAST")
    class Factory @Inject constructor(
        private val viewModelProvider: Provider<SourcesViewModel>,
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            require(modelClass == SourcesViewModel::class.java)
            return viewModelProvider.get() as T
        }
    }
}

sealed class SourcesUiState {

    data object Refresh : SourcesUiState()
    data object Loading : SourcesUiState()
    data class Success(val sources: List<Source>) : SourcesUiState()
    data class Error(val message: String) : SourcesUiState()
    data class Search(val sourcesByQuery: List<Source> = emptyList()) : SourcesUiState()
}

sealed class SourcesEvent {

    data object OnUpdateSources : SourcesEvent()
    data class OnSearchByQuery(val query: String) : SourcesEvent()
}