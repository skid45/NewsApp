package com.skid.newslistbysource

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.skid.filters.model.Language
import com.skid.filters.model.Sorting
import com.skid.filters.repository.FiltersRepository
import com.skid.news.model.Article
import com.skid.news.repository.NewsRepository
import com.skid.paging.Pager
import com.skid.utils.Constants.PAGE_SIZE
import com.skid.utils.asObservable
import com.skid.utils.format
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Provider

class NewsListBySourceViewModel @Inject constructor(
    private val newsRepository: NewsRepository,
    filtersRepository: FiltersRepository,
) : ViewModel() {

    private val disposables = CompositeDisposable()

    private val sourceId = BehaviorSubject.create<String?>()

    private val pager = BehaviorSubject
        .createDefault(getNewPager(source = sourceId.value))

    val newsPagerObservable = pager
        .switchMap { pager -> pager.loadNextPage() }
        .cache()

    private val combinedParameters = with(filtersRepository) {
        Observable.combineLatest(
            sourceId,
            getSortBy().asObservable(),
            getChosenDates().asObservable {
                Pair(
                    Calendar.getInstance().apply { timeInMillis = 0 },
                    Calendar.getInstance().apply { timeInMillis = 0 }
                )
            },
            getLanguage().asObservable { Language.NULL }
        ) { source, sortBy, chosenDates, language ->
            getNewPager(source, sortBy, chosenDates, language)
        }
    }

    init {
        val disposable = combinedParameters.subscribe { pager.onNext(it) }
        disposables.add(disposable)
    }

    private fun getNewPager(
        source: String? = null,
        sortBy: Sorting? = null,
        chosenDates: Pair<Calendar, Calendar>? = null,
        language: Language? = null,
    ): Pager<Article> {
        val newChosenDates = if (chosenDates == Pair(
                Calendar.getInstance().apply { timeInMillis = 0 },
                Calendar.getInstance().apply { timeInMillis = 0 }
            )
        ) null
        else chosenDates

        val newLanguage = if (language == Language.NULL) null else language

        return Pager(
            pageSize = PAGE_SIZE,
            initialPage = 1,
            pagingSourceFactory = {
                newsRepository.newsBySourcePagingSource(
                    source = source,
                    sortBy = sortBy?.apiName,
                    from = newChosenDates?.first?.format("yyyy MM dd"),
                    to = newChosenDates?.second?.format("yyyy MM dd"),
                    language = newLanguage?.apiName
                )
            }
        )
    }

    fun onSourceIdChanged(sourceId: String?) {
        this.sourceId.onNext(sourceId)
    }

    fun refreshPager() {
        sourceId.onNext(sourceId.value)
    }

    fun onLoadNextPage() {
        pager.onNext(pager.value)
    }

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }

    @Suppress("UNCHECKED_CAST")
    class Factory @Inject constructor(
        private val viewModelProvider: Provider<NewsListBySourceViewModel>,
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            require(modelClass == NewsListBySourceViewModel::class.java)
            return viewModelProvider.get() as T
        }
    }
}