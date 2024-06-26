package com.skid.filters

import androidx.annotation.IntRange
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.skid.filters.model.Filters
import com.skid.filters.model.Language
import com.skid.filters.model.Sorting
import com.skid.filters.repository.FiltersRepository
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Provider

class FiltersViewModel @Inject constructor(
    private val filtersRepository: FiltersRepository,
) : ViewModel() {

    private val _uiState = MutableLiveData(FiltersUiState())
    val uiState: LiveData<FiltersUiState> = _uiState

    init {
        viewModelScope.launch {
            filtersRepository
                .getFilters()
                .map { filters ->
                    FiltersUiState(
                        sortBy = filters.sortBy,
                        chosenDates = filters.chosenDates,
                        language = filters.language,
                        numberOfFilters = filters.numberOfFilters
                    )
                }.collect { _uiState.value = it }
        }
    }

    fun onEvent(event: FiltersEvent) {
        when (event) {
            is FiltersEvent.OnSortByChanged -> {
                val newState = uiState.value?.copy(sortBy = event.sortBy)
                _uiState.value = newState
            }

            is FiltersEvent.OnChosenDatesChanged -> {
                val newState = uiState.value?.copy(chosenDates = event.chosenDates)
                _uiState.value = newState
            }

            is FiltersEvent.OnLanguageChanged -> {
                val newState = uiState.value?.copy(language = event.language)
                _uiState.value = newState
            }

            is FiltersEvent.SaveFilters -> {
                viewModelScope.launch {
                    uiState.value?.let { uiState ->
                        filtersRepository.saveFilters(
                            Filters(
                                sortBy = uiState.sortBy,
                                chosenDates = uiState.chosenDates,
                                language = uiState.language,
                                numberOfFilters = calculateNumberOfFilters()
                            )
                        )
                    }
                }
            }
        }
    }

    private fun calculateNumberOfFilters(): Int {
        var numberOfFilters = 0
        uiState.value?.let { uiState ->
            if (uiState.sortBy != Sorting.NEW) numberOfFilters++
            if (uiState.chosenDates != null) numberOfFilters++
            if (uiState.language != null) numberOfFilters++
        }
        return numberOfFilters
    }

    @Suppress("UNCHECKED_CAST")
    class Factory @Inject constructor(
        private val viewModelProvider: Provider<FiltersViewModel>,
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            require(modelClass == FiltersViewModel::class.java)
            return viewModelProvider.get() as T
        }
    }
}


data class FiltersUiState(
    val sortBy: Sorting = Sorting.NEW,
    val chosenDates: Pair<Calendar, Calendar>? = null,
    val language: Language? = null,
    @IntRange(0, 3) val numberOfFilters: Int = 0,
)

sealed class FiltersEvent {
    data class OnSortByChanged(val sortBy: Sorting) : FiltersEvent()
    data class OnChosenDatesChanged(val chosenDates: Pair<Calendar, Calendar>?) : FiltersEvent()
    data class OnLanguageChanged(val language: Language?) : FiltersEvent()
    data object SaveFilters : FiltersEvent()
}