package com.skid.newslistbysource

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.skid.filters.model.Language
import com.skid.filters.model.Sorting
import com.skid.filters.repository.FiltersRepository
import com.skid.news.model.Article
import com.skid.news.repository.NewsRepository
import com.skid.news.usecase.GetNewsBySourcePagingSourceWithQueryUseCase
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
    private val getNewsBySourcePagingSourceWithQueryUseCase: GetNewsBySourcePagingSourceWithQueryUseCase,
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
            pager.onNext(
                getNewPager(
                    source = source,
                    sortBy = sortBy,
                    chosenDates = chosenDates,
                    language = language
                )
            )
        }
    }

    private val query = BehaviorSubject.create<String?>()

    private val searchPager = BehaviorSubject
        .createDefault(getNewPager(query = query.value, source = sourceId.value))

    val searchPagerObservable = searchPager
        .switchMap { pager -> pager.loadNextPage() }

    private val combinedSearchPagerParameters = Observable
        .combineLatest(query, sourceId) { query, source ->
            searchPager.onNext(getNewPagerWithQuery(query = query, source = source))
        }

    init {
        val disposable = combinedParameters.subscribe()
        val searchDisposable = combinedSearchPagerParameters.subscribe()
        disposables.add(disposable)
        disposables.add(searchDisposable)
    }

    private fun getNewPager(
        query: String? = null,
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
                    query = query,
                    source = source,
                    sortBy = sortBy?.apiName,
                    from = newChosenDates?.first?.format("yyyy MM dd"),
                    to = newChosenDates?.second?.format("yyyy MM dd"),
                    language = newLanguage?.apiName
                )
            }
        )
    }

    private fun getNewPagerWithQuery(
        query: String? = null,
        source: String? = null,
    ): Pager<Article> {
        return Pager(
            pageSize = PAGE_SIZE,
            initialPage = 1,
            pagingSourceFactory = {
                getNewsBySourcePagingSourceWithQueryUseCase(query = query, source = source)
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

    fun onLoadNextPageForSearch() {
        searchPager.onNext(searchPager.value)
    }

    fun onQueryChanged(query: String?) {
        this.query.onNext(query)
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