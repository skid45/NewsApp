package com.skid.newslistbysource

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.skid.filters.model.Filters
import com.skid.filters.model.Language
import com.skid.filters.model.Sorting
import com.skid.filters.repository.FiltersRepository
import com.skid.news.model.Article
import com.skid.news.repository.NewsRepository
import com.skid.news.usecase.GetNewsBySourcePagingSourceWithQueryUseCase
import com.skid.paging.Pager
import com.skid.paging.PagingData
import com.skid.utils.Constants.PAGE_SIZE
import com.skid.utils.asObservable
import com.skid.utils.format
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.ReplaySubject
import java.util.Calendar
import java.util.concurrent.TimeUnit

class NewsListBySourceViewModel @AssistedInject constructor(
    private val newsRepository: NewsRepository,
    filtersRepository: FiltersRepository,
    private val getNewsBySourcePagingSourceWithQueryUseCase: GetNewsBySourcePagingSourceWithQueryUseCase,
    @Assisted private val sourceId: String,
) : ViewModel() {

    private val disposables = CompositeDisposable()

    private val pager = BehaviorSubject
        .createDefault(getNewPager(source = sourceId))

    val pagingDataReplaySubject = ReplaySubject.create<PagingData<Article>>()

    private val filters = BehaviorSubject.create<Filters>()


    private val query = BehaviorSubject.create<String?>()

    private val searchPager = BehaviorSubject
        .createDefault(getNewPagerWithQuery(query = query.value, source = sourceId))

    val searchPagerObservable = searchPager
        .switchMap { pager -> pager.loadNextPage() }

    init {
        disposables.add(
            filtersRepository
                .getFilters()
                .asObservable()
                .subscribe(this.filters::onNext)
        )

        disposables.add(
            filters.subscribe { filters ->
                pagingDataReplaySubject.cleanupBuffer()
                pager.onNext(
                    getNewPager(
                        source = sourceId,
                        sortBy = filters.sortBy,
                        chosenDates = filters.chosenDates,
                        language = filters.language
                    )
                )
            }
        )

        disposables.add(
            pager
                .switchMap { pager -> pager.loadNextPage() }
                .subscribe { pagingData ->
                    pagingDataReplaySubject.onNext(pagingData)
                }
        )

        disposables.add(
            query
                .debounce(200, TimeUnit.MILLISECONDS)
                .subscribe { query ->
                    searchPager.onNext(getNewPagerWithQuery(query, sourceId))
                }
        )
    }

    private fun getNewPager(
        source: String? = null,
        sortBy: Sorting? = null,
        chosenDates: Pair<Calendar, Calendar>? = null,
        language: Language? = null,
    ): Pager<Article> {
        return Pager(
            pageSize = PAGE_SIZE,
            initialPage = 1,
            pagingSourceFactory = {
                newsRepository.newsBySourcePagingSource(
                    query = null,
                    source = source,
                    sortBy = sortBy?.apiName,
                    from = chosenDates?.first?.format("yyyy MM dd"),
                    to = chosenDates?.second?.format("yyyy MM dd"),
                    language = language?.apiName
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

    fun refreshPager() {
        pagingDataReplaySubject.cleanupBuffer()
        filters.onNext(filters.value)
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

    companion object {
        @Suppress("UNCHECKED_CAST")
        fun viewModelFactory(
            assistedFactory: Factory,
            sourceId: String,
        ): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return assistedFactory.create(sourceId) as T
                }
            }
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(sourceId: String): NewsListBySourceViewModel
    }
}