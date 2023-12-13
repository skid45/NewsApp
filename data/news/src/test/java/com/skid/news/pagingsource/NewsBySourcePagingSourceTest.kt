package com.skid.news.pagingsource

import com.skid.network.model.ArticleDTO
import com.skid.network.model.EverythingResponse
import com.skid.network.service.EverythingService
import com.skid.news.mapper.toArticle
import com.skid.news.model.Article
import com.skid.paging.LoadResult
import com.skid.utils.ResourceWrapper
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.rxjava3.core.Single
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import retrofit2.Response
import java.io.IOException

class NewsBySourcePagingSourceTest {

    @get:Rule
    val rule = MockKRule(this)

    private lateinit var newsBySourcePagingSource: NewsBySourcePagingSource

    @MockK
    lateinit var newsService: EverythingService

    @MockK
    lateinit var resourceWrapper: ResourceWrapper

    private fun setup(
        query: String? = null,
        source: String? = null,
        sortBy: String? = null,
        from: String? = null,
        to: String? = null,
        language: String? = null,
    ) {
        newsBySourcePagingSource = NewsBySourcePagingSource(
            newsService = newsService,
            resourceWrapper = resourceWrapper,
            query = query,
            source = source,
            sortBy = sortBy,
            from = from,
            to = to,
            language = language
        )
    }

    @Test
    fun `newsBySourcePagingSource with null params loadPage returns single with error`() {
        setup()
        val errorString = "TestError"
        every { resourceWrapper.getString(any()) } returns errorString
        every {
            newsService.getNews(page = any(), pageSize = any())
        } returns Single.just(Response.error(400, mockk(relaxed = true)))

        val result = newsBySourcePagingSource.loadPage(20, 1)

        verify { newsService.getNews(page = any(), pageSize = any()) }
        assertTrue(result.blockingGet() is LoadResult.Error<Article>)
        assertEquals(
            errorString,
            (result.blockingGet() as LoadResult.Error).e.localizedMessage
        )
        verify { resourceWrapper.getString(any()) }
    }

    @Test
    fun `newsBySourcePagingSource with non-null source and network connection loadPage returns single with page`() {
        val testSource = "test"
        setup(source = testSource)
        val mockList = List(3) {
            mockk<ArticleDTO>(relaxed = true) {
                every { publishedAt } returns "2023-11-18T14:00:00Z"
            }
        }
        every {
            newsService.getNews(source = testSource, page = any(), pageSize = any())
        } returns Single.just(Response.success(EverythingResponse(mockList)))

        val result = newsBySourcePagingSource.loadPage(20, 1)

        verify { newsService.getNews(source = testSource, page = any(), pageSize = any()) }
        assertTrue(result.blockingGet() is LoadResult.Page<Article>)
        assertEquals(
            LoadResult.Page(mockList.map(ArticleDTO::toArticle), 2),
            result.blockingGet()
        )
    }

    @Test
    fun `newsBySourcePagingSource with non-null source and without network connection loadPage returns single with error`() {
        val testSource = "test"
        val errorString = "TestError"
        every { resourceWrapper.getString(any()) } returns errorString
        setup(source = testSource)
        every {
            newsService.getNews(source = testSource, page = any(), pageSize = any())
        } returns Single.error(IOException())

        val result = newsBySourcePagingSource.loadPage(20, 1)

        verify { newsService.getNews(source = testSource, page = any(), pageSize = any()) }
        assertTrue(result.blockingGet() is LoadResult.Error<Article>)
        assertEquals(errorString, (result.blockingGet() as LoadResult.Error).e.localizedMessage)
        verify { resourceWrapper.getString(any()) }
    }

    @Test
    fun `newsBySourcePagingSource with non-null source and api response with 426 error code loadPage returns single with empty page`() {
        val testSource = "test"
        setup(source = testSource)
        every {
            newsService.getNews(source = testSource, page = more(5), pageSize = any())
        } returns Single.just(Response.error(426, mockk(relaxed = true)))

        val result = newsBySourcePagingSource.loadPage(20, 6)

        verify { newsService.getNews(source = testSource, page = more(5), pageSize = any()) }
        assertTrue(result.blockingGet() is LoadResult.Page<Article>)
        assertEquals(
            LoadResult.Page<Article>(emptyList(), 7),
            result.blockingGet()
        )
    }

    @Test
    fun `newsBySourcePagingSource calls getNews with correct args`() {
        val newsBySourcePagingSourceWithDefaultParams = NewsBySourcePagingSource(
            newsService, resourceWrapper, null, null, null, null, null, null
        )
        every {
            newsService.getNews()
        } returns Single.error(Exception())
        val newsBySourcePagingSourceWithNotNullParams = NewsBySourcePagingSource(
            newsService, resourceWrapper, "test", "test", "test", "test", "test", "test"
        )
        every {
            newsService.getNews(
                "test", "test", 5, 15, "test", "test", "test", "test"
            )
        } returns Single.error(Exception())

        newsBySourcePagingSourceWithDefaultParams.loadPage(100, 1)
        newsBySourcePagingSourceWithNotNullParams.loadPage(15, 5)

        verify(exactly = 2) {
            newsService.getNews(any(), any(), any(), any(), any(), any(), any(), any())
        }
        verify(exactly = 1) { newsService.getNews() }
        verify(exactly = 1) {
            newsService.getNews(
                "test", "test", 5, 15, "test", "test", "test", "test"
            )
        }
    }
}