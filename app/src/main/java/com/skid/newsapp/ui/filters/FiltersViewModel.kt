package com.skid.newsapp.ui.filters

import android.util.Log
import androidx.annotation.IntRange
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.skid.newsapp.domain.model.Language
import com.skid.newsapp.domain.model.Sorting
import com.skid.newsapp.domain.repository.FiltersRepository
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Provider

class FiltersViewModel @Inject constructor(
    private val filtersRepository: FiltersRepository,
) : ViewModel() {

    private val _uiState = MutableLiveData(FiltersUiState())
    val uiState: LiveData<FiltersUiState> get() = _uiState

    init {
        viewModelScope.launch {
            with(filtersRepository) {
                combine(
                    getSortBy(),
                    getChosenDates(),
                    getLanguages(),
                    getNumberOfFilters()
                ) { sortBy, chosenDates, languages, numberOfFilters ->
                    Log.d("TAG", "init: $sortBy, $chosenDates, $languages, $numberOfFilters")
                    FiltersUiState(
                        sortBy = sortBy,
                        chosenDates = chosenDates,
                        languages = languages,
                        numberOfFilters = numberOfFilters
                    )
                }.collect { _uiState.value = it }
            }
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

            is FiltersEvent.OnLanguagesChanged -> {
                val newState = uiState.value?.copy(languages = event.languages)
                _uiState.value = newState
            }

            is FiltersEvent.SaveFilters -> {
                viewModelScope.launch {
                    uiState.value?.let { uiState ->
                        filtersRepository.saveFilters(
                            sortBy = uiState.sortBy,
                            chosenDates = uiState.chosenDates,
                            languages = uiState.languages,
                            numberOfFilters = calculateNumberOfFilters()
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
            if (uiState.languages.isNotEmpty()) numberOfFilters++
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
    val languages: List<Language> = emptyList(),
    @IntRange(0, 3) val numberOfFilters: Int = 0,
)

sealed class FiltersEvent {
    data class OnSortByChanged(val sortBy: Sorting) : FiltersEvent()
    data class OnChosenDatesChanged(val chosenDates: Pair<Calendar, Calendar>?) : FiltersEvent()
    data class OnLanguagesChanged(val languages: List<Language>) : FiltersEvent()
    data object SaveFilters : FiltersEvent()
}