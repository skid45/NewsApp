package com.skid.news.pagingsource

import com.skid.database.sources.dao.CachedArticlesDao
import com.skid.database.sources.dao.SourcesDao
import com.skid.database.sources.model.CachedArticleEntity
import com.skid.database.sources.model.SourceEntity
import com.skid.network.model.ArticleDTO
import com.skid.network.model.EverythingResponse
import com.skid.network.model.SourceDTO
import com.skid.network.model.SourceForArticleDTO
import com.skid.network.model.SourcesResponse
import com.skid.network.service.EverythingService
import com.skid.network.service.SourcesService
import com.skid.news.mapper.toArticle
import com.skid.news.mapper.toCachedArticleEntity
import com.skid.news.mapper.toSourceEntity
import com.skid.news.model.Article
import com.skid.paging.LoadResult
import com.skid.utils.ResourceWrapper
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import retrofit2.Response
import java.io.IOException
import java.util.Calendar

class NewsByCategoryPagingSourceTest {

    @get:Rule
    val rule = MockKRule(this)

    private lateinit var newsByCategoryPagingSource: NewsByCategoryPagingSource

    @MockK
    lateinit var sourcesService: SourcesService

    @MockK
    lateinit var sourcesDao: SourcesDao

    @MockK
    lateinit var newsService: EverythingService

    @MockK
    lateinit var cachedArticlesDao: CachedArticlesDao

    @MockK
    lateinit var resourceWrapper: ResourceWrapper

    private fun setup(
        initialPage: Int = 1,
        category: String = "test",
        sortBy: String? = null,
        from: String? = null,
        to: String? = null,
        language: String? = null,
    ) {
        newsByCategoryPagingSource = NewsByCategoryPagingSource(
            sourcesService = sourcesService,
            sourcesDao = sourcesDao,
            newsService = newsService,
            cachedArticlesDao = cachedArticlesDao,
            resourceWrapper = resourceWrapper,
            initialPage = initialPage,
            category = category,
            sortBy = sortBy,
            from = from,
            to = to,
            language = language
        )

        every { resourceWrapper.getString(any()) } returns "test error string"
    }

    @Test
    fun `loadPage without internet connection and empty sources and cachedArticles tables in database returns single with error`() {
        setup()
        every {
            sourcesDao.getSourcesByCategory("test", any())
        } returns Single.just(emptyList())
        every { sourcesService.getSourcesSingle() } returns Single.error(IOException())
        every { cachedArticlesDao.isCacheEmpty() } returns Single.just(true)


        val result = newsByCategoryPagingSource.loadPage(20, 1)


        verify { sourcesDao.getSourcesByCategory("test", null) }
        assertTrue(result.blockingGet() is LoadResult.Error<Article>)
        assertEquals(
            "test error string",
            (result.blockingGet() as LoadResult.Error).e.localizedMessage
        )
        verify { sourcesService.getSourcesSingle() }
        verify { cachedArticlesDao.isCacheEmpty() }
        verify { resourceWrapper.getString(any()) }
    }

    @Test
    fun `loadPage without internet connection and non-empty sources table, empty cachedArticles table in database returns single with error`() {
        setup()
        val mockSourceList = List(3) { mockk<SourceEntity>(relaxed = true) }
        every { sourcesDao.getSourcesByCategory("test", any()) } returns Single.just(mockSourceList)
        every {
            newsService.getNews(source = any(), page = any(), pageSize = any())
        } returns Single.error(IOException())
        every { cachedArticlesDao.isCacheEmpty() } returns Single.just(true)


        val result = newsByCategoryPagingSource.loadPage(20, 1)


        verify { sourcesDao.getSourcesByCategory("test", null) }
        assertTrue(result.blockingGet() is LoadResult.Error<Article>)
        assertEquals(
            "test error string",
            (result.blockingGet() as LoadResult.Error).e.localizedMessage
        )
        verify {
            newsService.getNews(
                source = mockSourceList.map(SourceEntity::id).joinToString(","),
                page = 1,
                pageSize = 20
            )
        }
        verify { cachedArticlesDao.isCacheEmpty() }
        verify { resourceWrapper.getString(any()) }
    }

    @Test
    fun `loadPage without internet connection and non-empty sources and cachedArticles tables in database returns single with page`() {
        setup()
        val mockSourceList = List(3) { mockk<SourceEntity>(relaxed = true) }
        val mockCachedArticlesList = List(3) { mockk<CachedArticleEntity>(relaxed = true) }
        every { sourcesDao.getSourcesByCategory("test", any()) } returns Single.just(mockSourceList)
        every {
            newsService.getNews(source = any(), page = any(), pageSize = any())
        } returns Single.error(IOException())
        every { cachedArticlesDao.isCacheEmpty() } returns Single.just(false)
        every {
            cachedArticlesDao.getArticlesPageByCategory(
                any(), any(), "test", sourceNames = mockSourceList.map(SourceEntity::name)
            )
        } returns Single.just(mockCachedArticlesList)


        val result = newsByCategoryPagingSource.loadPage(20, 1)


        verify { sourcesDao.getSourcesByCategory("test", null) }
        assertEquals(
            LoadResult.Page(mockCachedArticlesList.map(CachedArticleEntity::toArticle), 2),
            result.blockingGet()
        )
        verify {
            newsService.getNews(
                source = mockSourceList.map(SourceEntity::id).joinToString(","),
                page = 1,
                pageSize = 20
            )
        }
        verify { cachedArticlesDao.isCacheEmpty() }
        verify {
            cachedArticlesDao.getArticlesPageByCategory(
                pageSize = 20,
                pageNumber = 0,
                category = "test",
                sourceNames = mockSourceList.map(SourceEntity::name)
            )
        }
    }

    @Test
    fun `loadPage with internet connection and empty sources and cachedArticles tables in database returns single with page`() {
        setup()
        val mockSourceList = List(3) { mockk<SourceDTO>(relaxed = true) }
        val mockArticlesList = List(3) {
            ArticleDTO(
                content = "test",
                description = "test",
                publishedAt = "2023-11-18T14:00:00Z",
                source = SourceForArticleDTO("test", "test"),
                title = "test",
                url = "test",
                urlToImage = "test"
            )
        }
        var callCounter = 0
        every {
            sourcesDao.getSourcesByCategory("test", any())
        } answers {
            if (callCounter == 0) {
                callCounter++
                Single.just(emptyList())
            } else {
                Single.just(mockSourceList.map(SourceDTO::toSourceEntity))
            }
        }
        every {
            sourcesService.getSourcesSingle()
        } returns Single.just(Response.success(SourcesResponse(mockSourceList)))
        every { sourcesDao.insertAllSourcesCompletable(any()) } returns Completable.complete()
        every {
            newsService.getNews(source = any(), page = any(), pageSize = any())
        } returns Single.just(Response.success(EverythingResponse(mockArticlesList)))
        justRun { cachedArticlesDao.deleteAllByCategory(any(), any()) }
        justRun { cachedArticlesDao.insertArticles(any()) }


        val result = newsByCategoryPagingSource.loadPage(20, 1)


        assertEquals(
            LoadResult.Page(mockArticlesList.map(ArticleDTO::toArticle), 2),
            result.blockingGet()
        )
        verify(atLeast = 2) { sourcesDao.getSourcesByCategory("test", null) }
        verify { sourcesService.getSourcesSingle() }
        verify { sourcesDao.insertAllSourcesCompletable(mockSourceList.map(SourceDTO::toSourceEntity)) }
        verify {
            newsService.getNews(
                source = mockSourceList.map(SourceDTO::id).joinToString(","),
                page = 1,
                pageSize = 20
            )
        }
        verify {
            cachedArticlesDao.deleteAllByCategory(
                "test",
                mockSourceList.map(SourceDTO::name)
            )
        }
        verify {
            cachedArticlesDao.insertArticles(
                mockArticlesList.map { it.toCachedArticleEntity("test") }
            )
        }
    }

    @Test
    fun `loadPage by page not equals initialPage with internet connection and empty sources and cachedArticles tables in database don't call deleted cachedArticles by category returns single with page`() {
        setup()
        val mockSourceList = List(3) { mockk<SourceDTO>(relaxed = true) }
        val mockArticlesList = List(3) {
            ArticleDTO(
                content = "test",
                description = "test",
                publishedAt = "2023-11-18T14:00:00Z",
                source = SourceForArticleDTO("test", "test"),
                title = "test",
                url = "test",
                urlToImage = "test"
            )
        }
        var callCounter = 0
        every {
            sourcesDao.getSourcesByCategory("test", any())
        } answers {
            if (callCounter == 0) {
                callCounter++
                Single.just(emptyList())
            } else {
                Single.just(mockSourceList.map(SourceDTO::toSourceEntity))
            }
        }
        every {
            sourcesService.getSourcesSingle()
        } returns Single.just(Response.success(SourcesResponse(mockSourceList)))
        every { sourcesDao.insertAllSourcesCompletable(any()) } returns Completable.complete()
        every {
            newsService.getNews(source = any(), page = any(), pageSize = any())
        } returns Single.just(Response.success(EverythingResponse(mockArticlesList)))
        justRun { cachedArticlesDao.insertArticles(any()) }


        val result = newsByCategoryPagingSource.loadPage(20, 2)


        assertEquals(
            LoadResult.Page(mockArticlesList.map(ArticleDTO::toArticle), 3),
            result.blockingGet()
        )
        verify(atLeast = 2) { sourcesDao.getSourcesByCategory("test", null) }
        verify { sourcesService.getSourcesSingle() }
        verify { sourcesDao.insertAllSourcesCompletable(mockSourceList.map(SourceDTO::toSourceEntity)) }
        verify {
            newsService.getNews(
                source = mockSourceList.map(SourceDTO::id).joinToString(","),
                page = 2,
                pageSize = 20
            )
        }
        verify(inverse = true) {
            cachedArticlesDao.deleteAllByCategory(
                "test",
                mockSourceList.map(SourceDTO::name)
            )
        }
        verify {
            cachedArticlesDao.insertArticles(
                mockArticlesList.map { it.toCachedArticleEntity("test") }
            )
        }
    }

    @Test
    fun `loadPage with sources endpoint error, empty sources and cachedArticles tables in database returns single with error`() {
        setup()
        every {
            sourcesDao.getSourcesByCategory("test", any())
        } returns Single.just(emptyList())
        every {
            sourcesService.getSourcesSingle()
        } returns Single.just(Response.error(404, mockk(relaxed = true)))
        every { cachedArticlesDao.isCacheEmpty() } returns Single.just(true)

        val result = newsByCategoryPagingSource.loadPage(20, 1)

        assertTrue(result.blockingGet() is LoadResult.Error<Article>)
        assertEquals(
            "test error string",
            (result.blockingGet() as LoadResult.Error).e.localizedMessage
        )
        verify { sourcesDao.getSourcesByCategory("test", null) }
        verify { sourcesService.getSourcesSingle() }
        verify { cachedArticlesDao.isCacheEmpty() }
    }

    @Test
    fun `loadPage with news endpoint error, non-empty sources table and empty cachedArticles table in database returns single with error`() {
        setup()
        val mockSourceList = List(3) { mockk<SourceEntity>(relaxed = true) }
        every {
            sourcesDao.getSourcesByCategory("test", any())
        } returns Single.just(mockSourceList)
        every {
            newsService.getNews(source = any(), page = any(), pageSize = any())
        } returns Single.just(Response.error(404, mockk(relaxed = true)))
        every { cachedArticlesDao.isCacheEmpty() } returns Single.just(true)


        val result = newsByCategoryPagingSource.loadPage(20, 1)


        assertTrue(result.blockingGet() is LoadResult.Error<Article>)
        assertEquals(
            "test error string",
            (result.blockingGet() as LoadResult.Error).e.localizedMessage
        )
        verify { sourcesDao.getSourcesByCategory("test", null) }
        verify {
            newsService.getNews(
                source = mockSourceList.map(SourceEntity::id).joinToString(","),
                page = 1,
                pageSize = 20
            )
        }
        verify { cachedArticlesDao.isCacheEmpty() }
    }

    @Test
    fun `loadPage with news endpoint error, non-empty sources and cachedArticles tables in database returns single with page from database`() {
        setup()
        val mockSourceList = List(3) { mockk<SourceEntity>(relaxed = true) }
        val mockCachedArticlesList = List(3) {
            CachedArticleEntity(
                content = "test",
                description = "test",
                publishedAt = Calendar.getInstance().apply { timeInMillis = 100 },
                sourceName = "test",
                title = "test",
                url = "test",
                imageUrl = "test",
                sourceDrawableId = 1,
                category = "test"
            )
        }
        every {
            sourcesDao.getSourcesByCategory("test", any())
        } returns Single.just(mockSourceList)
        every {
            newsService.getNews(source = any(), page = any(), pageSize = any())
        } returns Single.just(Response.error(404, mockk(relaxed = true)))
        every { cachedArticlesDao.isCacheEmpty() } returns Single.just(false)
        every {
            cachedArticlesDao.getArticlesPageByCategory(
                pageSize = any(), pageNumber = any(), category = "test", sourceNames = any()
            )
        } returns Single.just(mockCachedArticlesList)


        val result = newsByCategoryPagingSource.loadPage(20, 1)



        assertEquals(
            LoadResult.Page(mockCachedArticlesList.map(CachedArticleEntity::toArticle), 2),
            result.blockingGet()
        )
        verify { sourcesDao.getSourcesByCategory("test", null) }
        verify {
            newsService.getNews(
                source = mockSourceList.map(SourceEntity::id).joinToString(","),
                page = 1,
                pageSize = 20
            )
        }
        verify { cachedArticlesDao.isCacheEmpty() }
        verify {
            cachedArticlesDao.getArticlesPageByCategory(
                pageSize = 20,
                pageNumber = 0,
                category = "test",
                sourceNames = mockSourceList.map(SourceEntity::name)
            )
        }
    }
}