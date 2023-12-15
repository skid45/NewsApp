package com.skid.headlines

import com.skid.filters.model.Filters
import com.skid.filters.model.Language
import com.skid.filters.model.Sorting
import com.skid.filters.repository.FiltersRepository
import com.skid.headlines.newsbycategory.NewsByCategoryPresenter
import com.skid.headlines.newsbycategory.NewsByCategoryView
import com.skid.news.model.Article
import com.skid.news.pagingsource.NewsByCategoryPagingSource
import com.skid.news.repository.NewsRepository
import com.skid.paging.LoadResult
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit4.MockKRule
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.rxjava3.android.plugins.RxAndroidPlugins
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.plugins.RxJavaPlugins
import io.reactivex.rxjava3.schedulers.TestScheduler
import kotlinx.coroutines.flow.flowOf
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class NewsByCategoryPresenterTest {

    @get:Rule
    val rule = MockKRule(this)

    private lateinit var newsByCategoryPresenter: NewsByCategoryPresenter

    @RelaxedMockK
    lateinit var newsRepository: NewsRepository

    @RelaxedMockK
    lateinit var newsByCategoryPagingSource: NewsByCategoryPagingSource

    @RelaxedMockK
    lateinit var filtersRepository: FiltersRepository

    @RelaxedMockK
    lateinit var headlinesRouter: HeadlinesRouter

    @RelaxedMockK
    lateinit var view: NewsByCategoryView

    private lateinit var testScheduler: TestScheduler

    @Before
    fun setup() {
        newsByCategoryPresenter = NewsByCategoryPresenter(
            /* newsRepository = */ newsRepository,
            /* filtersRepository = */ filtersRepository,
            /* router = */ headlinesRouter,
            /* category = */ "test category"
        )
        testScheduler = TestScheduler()
        RxJavaPlugins.setInitIoSchedulerHandler { testScheduler }
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { testScheduler }
        RxJavaPlugins.setIoSchedulerHandler { testScheduler }
        RxAndroidPlugins.setMainThreadSchedulerHandler { testScheduler }

        every {
            newsRepository.newsByCategoryPagingSource(any(), any(), any(), any(), any(), any())
        } returns newsByCategoryPagingSource
        every { filtersRepository.getFilters() } returns mockk(relaxed = true)
    }

    @After
    fun teardown() {
        RxJavaPlugins.reset()
        RxAndroidPlugins.reset()
    }

    @Test
    fun `onFirstViewAttach calls showProgress(true)`() {
        newsByCategoryPresenter.attachView(view)
        testScheduler.triggerActions()

        verify { view.showProgress(true) }
    }

    @Test
    fun `call onRefresh without errors calls showProgress(false), submitPage and hideRefresh`() {
        every {
            newsByCategoryPagingSource.loadPage(any(), any())
        } returns Single.just(LoadResult.Page(emptyList(), 2))

        newsByCategoryPresenter.attachView(view)
        newsByCategoryPresenter.onRefresh()
        testScheduler.triggerActions()

        verify(exactly = 1) { view.showProgress(true) }
        verify(exactly = 1) { view.showProgress(false) }
        verify(exactly = 1) { view.submitPage(any()) }
        verify(exactly = 1) { view.hideRefresh() }
        verify {
            newsRepository.newsByCategoryPagingSource(any(), any(), any(), any(), any(), any())
        }
        verify(exactly = 1) {
            newsByCategoryPagingSource.loadPage(any(), any())
        }
    }

    @Test
    fun `call onLoadNextPage without error calls showProgress(false), submitPage and hideRefresh, loadPage`() {
        every {
            newsByCategoryPagingSource.loadPage(any(), any())
        } returns Single.just(LoadResult.Page(emptyList(), 2))

        newsByCategoryPresenter.attachView(view)
        newsByCategoryPresenter.onLoadNextPage()
        testScheduler.triggerActions()

        verify(exactly = 1) { view.showProgress(true) }
        verify(exactly = 1) { view.showProgress(false) }
        verify(exactly = 1) { view.submitPage(any()) }
        verify(exactly = 1) { view.hideRefresh() }
        verify(exactly = 1) {
            newsRepository.newsByCategoryPagingSource(any(), any(), any(), any(), any(), any())
        }
        verify(exactly = 1) {
            newsByCategoryPagingSource.loadPage(any(), any())
        }
    }

    @Test
    fun `multiple call onLoadNextPage without error calls showProgress(false), submitPage and hideRefresh, loadPage`() {
        every {
            newsByCategoryPagingSource.loadPage(any(), any())
        } returns Single.just(LoadResult.Page(emptyList(), 2))

        newsByCategoryPresenter.attachView(view)
        newsByCategoryPresenter.onLoadNextPage()
        testScheduler.triggerActions()
        newsByCategoryPresenter.onLoadNextPage()
        testScheduler.triggerActions()
        newsByCategoryPresenter.onLoadNextPage()
        testScheduler.triggerActions()

        verify(exactly = 1) { view.showProgress(true) }
        verify(exactly = 3) { view.showProgress(false) }
        verify(exactly = 3) { view.submitPage(any()) }
        verify(exactly = 3) { view.hideRefresh() }
        verify(exactly = 3) {
            newsRepository.newsByCategoryPagingSource(any(), any(), any(), any(), any(), any())
        }
        verify(exactly = 3) {
            newsByCategoryPagingSource.loadPage(any(), any())
        }
    }

    @Test
    fun `call onLoadNextPage with error calls onError`() {
        every {
            newsByCategoryPagingSource.loadPage(any(), any())
        } returns Single.just(LoadResult.Error(Exception("test error string")))

        newsByCategoryPresenter.attachView(view)
        newsByCategoryPresenter.onLoadNextPage()
        testScheduler.triggerActions()

        verify(exactly = 1) { view.showProgress(true) }
        verify(exactly = 1) { newsByCategoryPresenter.onError("test error string") }
        verify(exactly = 1) { headlinesRouter.onError("test error string") }
        verify(inverse = true) { view.showProgress(false) }
        verify(inverse = true) { view.submitPage(any()) }
        verify(inverse = true) { view.hideRefresh() }
        verify(exactly = 1) {
            newsRepository.newsByCategoryPagingSource(any(), any(), any(), any(), any(), any())
        }
        verify(exactly = 1) {
            newsByCategoryPagingSource.loadPage(any(), any())
        }
    }

    @Test
    fun `filters changing calls create new newsByCategoryPagingSource with correct params`() {
        every {
            newsByCategoryPagingSource.loadPage(any(), any())
        } returns Single.just(LoadResult.Page(emptyList(), 2))
        every {
            filtersRepository.getFilters()
        } returns flowOf(
            Filters(
                sortBy = Sorting.POPULAR,
                chosenDates = null,
                language = Language.RUSSIAN,
                numberOfFilters = 2
            )
        )

        newsByCategoryPresenter.attachView(view)
        testScheduler.triggerActions()

        verify(exactly = 1) { view.showProgress(true) }
        verify(exactly = 2) {
            newsRepository.newsByCategoryPagingSource(any(), any(), any(), any(), any(), any())
        }
        verify(exactly = 1) {
            newsRepository.newsByCategoryPagingSource(
                initialPage = 1,
                category = "test category",
                sortBy = Sorting.NEW.apiName,
                from = null,
                to = null,
                language = null
            )
        }
        verify(exactly = 1) {
            newsRepository.newsByCategoryPagingSource(
                initialPage = 1,
                category = "test category",
                sortBy = Sorting.POPULAR.apiName,
                from = null,
                to = null,
                language = Language.RUSSIAN.apiName
            )
        }
    }

    @Test
    fun `call onArticleProfile calls router onArticleProfile with correct params`() {
        val mockArticle = mockk<Article>(relaxed = true) {
            every { url } returns "test"
        }

        newsByCategoryPresenter.attachView(view)
        testScheduler.triggerActions()
        newsByCategoryPresenter.onArticleProfile(mockArticle)

        verify(exactly = 1) { view.showProgress(true) }
        verify { headlinesRouter.onArticleProfile(mockArticle) }
    }
}