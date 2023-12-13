package com.skid.news.repository

import com.skid.news.pagingsource.NewsByCategoryPagingSource
import com.skid.news.pagingsource.NewsByQueryPagingSource
import com.skid.news.pagingsource.NewsBySourcePagingSource
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit4.MockKRule
import io.mockk.mockk
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class NewsRepositoryImplTest {

    @get:Rule
    val rule = MockKRule(this)

    private lateinit var newsRepositoryImpl: NewsRepositoryImpl

    @RelaxedMockK
    lateinit var newsBySourcePagingSourceFactory: NewsBySourcePagingSource.Factory

    @RelaxedMockK
    lateinit var newsByCategoryPagingSourceFactory: NewsByCategoryPagingSource.Factory

    @RelaxedMockK
    lateinit var newsByQueryPagingSourceFactory: NewsByQueryPagingSource.Factory

    @Before
    fun setup() {
        newsRepositoryImpl = NewsRepositoryImpl(
            newsBySourcePagingSourceFactory = newsBySourcePagingSourceFactory,
            newsByCategoryPagingSourceFactory = newsByCategoryPagingSourceFactory,
            newsByQueryPagingSourceFactory = newsByQueryPagingSourceFactory
        )
    }


    @Test
    fun `newsBySourcePagingSource calls with correct args`() {
        every {
            newsBySourcePagingSourceFactory.create(
                any(), any(), any(), any(), any(), any()
            )
        } returns mockk()

        newsRepositoryImpl.newsBySourcePagingSource(
            null, null, null, null, null, null
        )
        newsRepositoryImpl.newsBySourcePagingSource(
            "test", "test", "test", "test", "test", "test"
        )

        verify(exactly = 2) {
            newsBySourcePagingSourceFactory.create(
                any(), any(), any(), any(), any(), any()
            )
        }
        verify(exactly = 1) {
            newsBySourcePagingSourceFactory.create(
                null, null, null, null, null, null
            )
        }
        verify(exactly = 1) {
            newsBySourcePagingSourceFactory.create(
                "test", "test", "test", "test", "test", "test"
            )
        }
    }

    @Test
    fun `newsBySourcePagingSource with null params returns correct newsBySourcePagingSource`() {
        val mockNewsBySourcePagingSource = NewsBySourcePagingSource(
            mockk(), mockk(), null, null, null, null, null, null
        )
        every {
            newsBySourcePagingSourceFactory.create(
                null, null, null, null, null, null
            )
        } returns mockNewsBySourcePagingSource

        val result = newsRepositoryImpl.newsBySourcePagingSource(
            null, null, null, null, null, null
        )

        assertEquals(mockNewsBySourcePagingSource, result)
    }

    @Test
    fun `newsBySourcePagingSource with non-null params returns correct newsBySourcePagingSource`() {
        val mockNewsBySourcePagingSource = NewsBySourcePagingSource(
            mockk(), mockk(), "test", "test", "test", "test", "test", "test"
        )
        every {
            newsBySourcePagingSourceFactory.create(
                "test", "test", "test", "test", "test", "test"
            )
        } returns mockNewsBySourcePagingSource

        val result = newsRepositoryImpl.newsBySourcePagingSource(
            "test", "test", "test", "test", "test", "test"
        )

        assertEquals(mockNewsBySourcePagingSource, result)
    }

    @Test
    fun `newsByCategoryPagingSource calls with correct args`() {
        every {
            newsByCategoryPagingSourceFactory.create(
                any(), any(), any(), any(), any(), any()
            )
        } returns mockk()

        newsRepositoryImpl.newsByCategoryPagingSource(
            0, "test", null, null, null, null
        )
        newsRepositoryImpl.newsByCategoryPagingSource(
            0, "test", "test", "test", "test", "test"
        )

        verify(exactly = 2) {
            newsByCategoryPagingSourceFactory.create(
                any(), any(), any(), any(), any(), any()
            )
        }
        verify(exactly = 1) {
            newsByCategoryPagingSourceFactory.create(
                0, "test", null, null, null, null
            )
        }
        verify(exactly = 1) {
            newsByCategoryPagingSourceFactory.create(
                0, "test", "test", "test", "test", "test"
            )
        }
    }

    @Test
    fun `newsByCategoryPagingSource with null params returns correct newsByCategoryPagingSource`() {
        val mockNewsByCategoryPagingSource = NewsByCategoryPagingSource(
            sourcesService = mockk(),
            sourcesDao = mockk(),
            newsService = mockk(),
            cachedArticlesDao = mockk(),
            resourceWrapper = mockk(),
            initialPage = 0,
            category = "test",
            sortBy = null,
            from = null,
            to = null,
            language = null
        )
        every {
            newsByCategoryPagingSourceFactory.create(
                0, "test", null, null, null, null
            )
        } returns mockNewsByCategoryPagingSource

        val result = newsRepositoryImpl.newsByCategoryPagingSource(
            0, "test", null, null, null, null
        )

        assertEquals(mockNewsByCategoryPagingSource, result)
    }

    @Test
    fun `newsByCategoryPagingSource with non-null params returns correct newsByCategoryPagingSource`() {
        val mockNewsByCategoryPagingSource = NewsByCategoryPagingSource(
            sourcesService = mockk(),
            sourcesDao = mockk(),
            newsService = mockk(),
            cachedArticlesDao = mockk(),
            resourceWrapper = mockk(),
            initialPage = 0,
            category = "test",
            sortBy = "test",
            from = "test",
            to = "test",
            language = "test"
        )
        every {
            newsByCategoryPagingSourceFactory.create(
                0, "test", "test", "test", "test", "test"
            )
        } returns mockNewsByCategoryPagingSource

        val result = newsRepositoryImpl.newsByCategoryPagingSource(
            0, "test", "test", "test", "test", "test"
        )

        assertEquals(mockNewsByCategoryPagingSource, result)
    }

    @Test
    fun `newsByQueryPagingSource calls with correct params and returns correct newsByQueryPagingSource`() {
        val mockNewsByQueryPagingSource = NewsByQueryPagingSource(
            mockk(), mockk(), mockk(), 0, "test"
        )
        every {
            newsByQueryPagingSourceFactory.create(0, "test")
        } returns mockNewsByQueryPagingSource

        val result = newsRepositoryImpl.newsByQueryPagingSource(0, "test")

        verify { newsByQueryPagingSourceFactory.create(0, "test") }
        assertEquals(mockNewsByQueryPagingSource, result)
    }
}