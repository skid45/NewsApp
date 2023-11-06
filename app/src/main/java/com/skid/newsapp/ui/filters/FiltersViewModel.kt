package com.skid.newsapp.ui.filters

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.skid.newsapp.domain.model.Language
import com.skid.newsapp.domain.model.Sorting
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Provider

class FiltersViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableLiveData(FiltersUiState())
    val uiState: LiveData<FiltersUiState> get() = _uiState

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
                //TODO(Saving filters)
            }
        }
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
)

sealed class FiltersEvent {
    data class OnSortByChanged(val sortBy: Sorting) : FiltersEvent()
    data class OnChosenDatesChanged(val chosenDates: Pair<Calendar, Calendar>) : FiltersEvent()
    data class OnLanguagesChanged(val languages: List<Language>) : FiltersEvent()
    data object SaveFilters : FiltersEvent()
}