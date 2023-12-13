package com.skid.news.pagingsource

import com.skid.database.sources.dao.CachedArticlesDao
import com.skid.database.sources.model.CachedArticleEntity
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

class NewsByQueryPagingSourceTest {

    @get:Rule
    val rule = MockKRule(this)

    private lateinit var newsByQueryPagingSource: NewsByQueryPagingSource

    @MockK
    lateinit var newsService: EverythingService

    @MockK
    lateinit var cachedArticlesDao: CachedArticlesDao

    @MockK
    lateinit var resourceWrapper: ResourceWrapper

    private fun setup(query: String = "test", initialPage: Int = 1) {
        newsByQueryPagingSource = NewsByQueryPagingSource(
            newsService = newsService,
            cachedArticlesDao = cachedArticlesDao,
            resourceWrapper = resourceWrapper,
            initialPage = initialPage,
            query = query
        )

        every { resourceWrapper.getString(any()) } returns "test error string"
    }

    @Test
    fun `loadPage by blank query returns single with empty page`() {
        setup("    \n   ")

        val result = newsByQueryPagingSource.loadPage(20, 1)

        assertEquals(
            LoadResult.Page<Article>(emptyList(), 2),
            result.blockingGet()
        )
    }

    @Test
    fun `loadPage by non-blank query with network connection calls getNews with correct args and returns single with page`() {
        setup()
        val mockResponse = EverythingResponse(
            List(3) {
                mockk(relaxed = true) {
                    every { title } returns "test"
                    every { publishedAt } returns "2023-11-18T14:00:00Z"
                }
            }
        )
        every {
            newsService.getNews(query = "test", pageSize = any(), page = any())
        } returns Single.just(Response.success(mockResponse))

        val result = newsByQueryPagingSource.loadPage(20, 1)

        verify { newsService.getNews(query = "test", pageSize = 20, page = 1) }
        assertEquals(
            LoadResult.Page(mockResponse.articles.map(ArticleDTO::toArticle), 2),
            result.blockingGet()
        )
    }

    @Test
    fun `loadPage by non-blank query with api error and empty database returns single with error`() {
        setup()
        every {
            newsService.getNews(query = "test", pageSize = any(), page = any())
        } returns Single.just(Response.error(404, mockk(relaxed = true)))
        every { cachedArticlesDao.isCacheEmpty() } returns Single.just(true)

        val result = newsByQueryPagingSource.loadPage(20, 1)

        verify { newsService.getNews(query = "test", pageSize = 20, page = 1) }
        assertTrue(result.blockingGet() is LoadResult.Error<Article>)
        assertEquals(
            "test error string",
            (result.blockingGet() as LoadResult.Error).e.localizedMessage
        )
        verify { cachedArticlesDao.isCacheEmpty() }
        verify { resourceWrapper.getString(any()) }
    }

    @Test
    fun `loadPage by non-blank query with api error and non-empty database returns single with page from database`() {
        setup()
        val mockCachedArticleEntityList = List(3) { mockk<CachedArticleEntity>(relaxed = true) }
        every {
            newsService.getNews(query = "test", pageSize = any(), page = any())
        } returns Single.just(Response.error(404, mockk(relaxed = true)))
        every { cachedArticlesDao.isCacheEmpty() } returns Single.just(false)
        every {
            cachedArticlesDao.getArticlesPageByQuery(20, 0, "test")
        } returns Single.just(mockCachedArticleEntityList)

        val result = newsByQueryPagingSource.loadPage(20, 1)

        verify { newsService.getNews(query = "test", pageSize = 20, page = 1) }
        assertEquals(
            LoadResult.Page(mockCachedArticleEntityList.map(CachedArticleEntity::toArticle), 2),
            result.blockingGet()
        )
        verify { cachedArticlesDao.isCacheEmpty() }
        verify { cachedArticlesDao.getArticlesPageByQuery(20, 0, "test") }
    }

    @Test
    fun `loadPage by non-blank query without internet connection and empty database returns single with error`() {
        setup()
        every {
            newsService.getNews(query = "test", pageSize = any(), page = any())
        } returns Single.error(IOException())
        every { cachedArticlesDao.isCacheEmpty() } returns Single.just(true)

        val result = newsByQueryPagingSource.loadPage(20, 1)

        verify { newsService.getNews(query = "test", pageSize = 20, page = 1) }
        assertTrue(result.blockingGet() is LoadResult.Error<Article>)
        assertEquals(
            "test error string",
            (result.blockingGet() as LoadResult.Error).e.localizedMessage
        )
        verify { cachedArticlesDao.isCacheEmpty() }
        verify { resourceWrapper.getString(any()) }
    }

    @Test
    fun `loadPage by non-blank query without internet connection and non-empty database returns single with page from database`() {
        setup()
        val mockCachedArticleEntityList = List(3) { mockk<CachedArticleEntity>(relaxed = true) }
        every {
            newsService.getNews(query = "test", pageSize = any(), page = any())
        } returns Single.error(IOException())
        every { cachedArticlesDao.isCacheEmpty() } returns Single.just(false)
        every {
            cachedArticlesDao.getArticlesPageByQuery(any(), any(), "test")
        } returns Single.just(mockCachedArticleEntityList)

        val result = newsByQueryPagingSource.loadPage(20, 1)

        verify { newsService.getNews(query = "test", pageSize = 20, page = 1) }
        assertEquals(
            LoadResult.Page(mockCachedArticleEntityList.map(CachedArticleEntity::toArticle), 2),
            result.blockingGet()
        )
        verify { cachedArticlesDao.isCacheEmpty() }
        verify { resourceWrapper.getString(any()) }
    }
}