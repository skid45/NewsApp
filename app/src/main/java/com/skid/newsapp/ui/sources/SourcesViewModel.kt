package com.skid.newsapp.ui.sources

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.skid.newsapp.domain.model.Source
import com.skid.newsapp.domain.repository.SourcesRepository
import com.skid.newsapp.domain.usecase.GetSourcesByQueryUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider

class SourcesViewModel @Inject constructor(
    private val sourcesRepository: SourcesRepository,
    private val getSourcesByQueryUseCase: GetSourcesByQueryUseCase,
) : ViewModel() {

    private val _sources = MutableStateFlow(emptyList<Source>())
    val sources = _sources.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    private val _query = MutableStateFlow("")
    val query = _query.asStateFlow()

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    val sourcesByQuery: StateFlow<List<Source>> = query
        .debounce(200)
        .mapLatest { query ->
            val result = getSourcesByQueryUseCase(query)
            if (result.isSuccess) {
                result.getOrThrow()
            } else {
                _error.value = result.exceptionOrNull()?.localizedMessage
                emptyList()
            }
        }
        .flowOn(Dispatchers.Default)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    init {
        updateSources()
    }

    fun updateSources() {
        viewModelScope.launch {
            val result = sourcesRepository.getSources()
            if (result.isSuccess) {
                _sources.value = result.getOrThrow()
            } else {
                _error.value = result.exceptionOrNull()?.localizedMessage
            }
        }
    }

    fun onQueryChanged(query: String) {
        _query.value = query
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