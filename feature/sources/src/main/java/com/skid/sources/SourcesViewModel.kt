package com.skid.sources

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.skid.sources.model.Source
import com.skid.sources.repository.SourcesRepository
import com.skid.sources.usecase.GetSourcesByQueryUseCase
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider

@OptIn(FlowPreview::class)
class SourcesViewModel @Inject constructor(
    private val sourcesRepository: SourcesRepository,
    private val getSourcesByQueryUseCase: GetSourcesByQueryUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow<SourcesUiState>(SourcesUiState.Loading)
    val uiState = _uiState.asStateFlow()

    private val _query = MutableSharedFlow<String>(replay = 1)
    private val query = _query
        .debounce(100)
        .distinctUntilChanged()
        .shareIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 1)

    init {
        onEvent(SourcesEvent.OnUpdateSources)

        viewModelScope.launch {
            query.collect { onEvent(SourcesEvent.OnSearchByQuery(it)) }
        }
    }

    fun onEvent(event: SourcesEvent) {
        when (event) {
            SourcesEvent.OnUpdateSources -> {
                viewModelScope.launch {
                    _uiState.value =
                        when (uiState.value) {
                            is SourcesUiState.Success -> SourcesUiState.Refresh
                            is SourcesUiState.Search -> {
                                delay(100)
                                SourcesUiState.Loading
                            }

                            else -> SourcesUiState.Loading
                        }
                }
                updateSources()
            }

            is SourcesEvent.OnSearchByQuery -> searchByQuery(event.query)
            is SourcesEvent.OnQueryChanged -> {
                viewModelScope.launch {
                    _query.emit(event.query)
                }
            }
        }
    }

    private fun updateSources() {
        viewModelScope.launch {
            val result = sourcesRepository.getSources()
            _uiState.value =
                if (result.isSuccess) SourcesUiState.Success(result.getOrThrow())
                else SourcesUiState.Error(result.exceptionOrNull()?.localizedMessage ?: "")
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
    data class OnQueryChanged(val query: String) : SourcesEvent()
}