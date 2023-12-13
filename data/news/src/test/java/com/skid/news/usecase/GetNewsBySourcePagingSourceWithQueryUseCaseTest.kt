package com.skid.news.usecase

import com.skid.news.pagingsource.NewsBySourcePagingSource
import com.skid.news.repository.NewsRepository
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import io.mockk.mockk
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class GetNewsBySourcePagingSourceWithQueryUseCaseTest {

    @get:Rule
    val rule = MockKRule(this)

    private lateinit var getNewsBySourcePagingSourceWithQueryUseCase: GetNewsBySourcePagingSourceWithQueryUseCase

    @MockK
    lateinit var newsRepository: NewsRepository

    @Before
    fun setup() {
        getNewsBySourcePagingSourceWithQueryUseCase = GetNewsBySourcePagingSourceWithQueryUseCase(
            newsRepository = newsRepository
        )
    }

    @Test
    fun `get paging source by null query returns newsBySourcePagingSource with null params`() {
        val query: String? = null
        val mockNewsBySourcePagingSource = NewsBySourcePagingSource(
            newsService = mockk(),
            resourceWrapper = mockk(),
            query = query,
            source = null,
            sortBy = null,
            from = null,
            to = null,
            language = null
        )
        every {
            newsRepository.newsBySourcePagingSource(null, any(), any(), any(), any(), any())
        } returns mockNewsBySourcePagingSource

        val result = getNewsBySourcePagingSourceWithQueryUseCase.invoke(query, null)

        verify {
            newsRepository.newsBySourcePagingSource(
                query = null,
                source = null,
                sortBy = null,
                from = null,
                to = null,
                language = null
            )
        }
        assertEquals(mockNewsBySourcePagingSource, result)
    }

    @Test
    fun `get paging source by blank query returns newsBySourcePagingSource with null params`() {
        val query = "    \n       "
        val mockNewsBySourcePagingSource = NewsBySourcePagingSource(
            newsService = mockk(),
            resourceWrapper = mockk(),
            query = null,
            source = null,
            sortBy = null,
            from = null,
            to = null,
            language = null
        )
        every {
            newsRepository.newsBySourcePagingSource(null, any(), any(), any(), any(), any())
        } returns mockNewsBySourcePagingSource

        val result = getNewsBySourcePagingSourceWithQueryUseCase.invoke(query, null)

        verify {
            newsRepository.newsBySourcePagingSource(
                query = null,
                source = null,
                sortBy = null,
                from = null,
                to = null,
                language = null
            )
        }
        assertEquals(mockNewsBySourcePagingSource, result)
    }

    @Test
    fun `get paging source by non-blank and non-null query returns newsBySourcePagingSource with not null query param`() {
        val query = "test"
        val mockNewsBySourcePagingSource = NewsBySourcePagingSource(
            newsService = mockk(),
            resourceWrapper = mockk(),
            query = query,
            source = null,
            sortBy = null,
            from = null,
            to = null,
            language = null
        )
        every {
            newsRepository.newsBySourcePagingSource(query, any(), any(), any(), any(), any())
        } returns mockNewsBySourcePagingSource

        val result = getNewsBySourcePagingSourceWithQueryUseCase.invoke(query, null)

        verify { newsRepository.newsBySourcePagingSource(query, null, null, null, null, null) }
        assertEquals(mockNewsBySourcePagingSource, result)
    }

    @Test
    fun `get paging source by non-null source returns newsBySourcePagingSource with not null source param`() {
        val query = "test"
        val source = "testSource"
        val mockNewsBySourcePagingSource = NewsBySourcePagingSource(
            newsService = mockk(),
            resourceWrapper = mockk(),
            query = query,
            source = source,
            sortBy = null,
            from = null,
            to = null,
            language = null
        )
        every {
            newsRepository.newsBySourcePagingSource(query, source, any(), any(), any(), any())
        } returns mockNewsBySourcePagingSource

        val result = getNewsBySourcePagingSourceWithQueryUseCase.invoke(query, source)

        verify { newsRepository.newsBySourcePagingSource(query, source, null, null, null, null) }
        assertEquals(mockNewsBySourcePagingSource, result)
    }

    @Test
    fun `get paging source by non-null source and null query returns newsBySourcePagingSource with null params`() {
        val source = "testSource"
        val mockNewsBySourcePagingSource = NewsBySourcePagingSource(
            newsService = mockk(),
            resourceWrapper = mockk(),
            query = null,
            source = null,
            sortBy = null,
            from = null,
            to = null,
            language = null
        )
        every {
            newsRepository.newsBySourcePagingSource(null, null, any(), any(), any(), any())
        } returns mockNewsBySourcePagingSource

        val result = getNewsBySourcePagingSourceWithQueryUseCase.invoke(null, source)

        verify { newsRepository.newsBySourcePagingSource(null, null, null, null, null, null) }
        assertEquals(mockNewsBySourcePagingSource, result)
    }
}