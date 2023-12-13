package com.skid.sources

import com.skid.sources.model.Source
import com.skid.sources.repository.SourcesRepository
import com.skid.sources.usecase.GetSourcesByQueryUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class GetSourcesByQueryUseCaseTest {

    @get:Rule
    val rule = MockKRule(this)

    @MockK
    lateinit var sourcesRepository: SourcesRepository

    private lateinit var getSourcesByQueryUseCase: GetSourcesByQueryUseCase


    @Before
    fun setup() {
        getSourcesByQueryUseCase = GetSourcesByQueryUseCase(
            sourcesRepository = sourcesRepository
        )
    }

    @Test
    fun `get sources by empty query returns success result with empty list`() {
        val query = ""

        val result = runBlocking { getSourcesByQueryUseCase.invoke(query) }

        assertTrue(result.isSuccess)
        assertEquals(Result.success(emptyList<Source>()), result)
    }

    @Test
    fun `get sources by blank query returns success result with empty list`() {
        val query = "        \n   "

        val result = runBlocking { getSourcesByQueryUseCase.invoke(query) }

        assertTrue(result.isSuccess)
        assertEquals(Result.success(emptyList<Source>()), result)
    }

    @Test
    fun `get sources by non-empty query returns success with non-empty list`() {
        val query = "test"
        val mockSources = listOf(Source("test", "test", "test", "test", 0))
        coEvery {
            sourcesRepository.getSources(refresh = any(), language = any(), query = query)
        } returns Result.success(mockSources)

        val result =
            runBlocking { getSourcesByQueryUseCase.invoke(query) }

        assertTrue(result.isSuccess)
        assertTrue(result.getOrThrow().isNotEmpty())
    }

    @Test
    fun `get sources by non-empty query returns success with list of source matching query`() {
        val query = "test"
        val matchingSource = Source(
            id = "test",
            name = "test",
            category = "test",
            country = "test",
            drawableResId = 0
        )
        val nonMatchingSource = Source(
            id = "other",
            name = "other",
            category = "other",
            country = "other",
            drawableResId = 0
        )
        val allSources = listOf(matchingSource, nonMatchingSource)
        coEvery {
            sourcesRepository.getSources(refresh = any(), language = any(), query = query)
        } returns Result.success(allSources.filter { it.name == query })

        val result = runBlocking { getSourcesByQueryUseCase.invoke(query) }

        assertTrue(result.isSuccess)
        assertTrue(result.getOrThrow().contains(matchingSource))
        assertFalse(result.getOrThrow().contains(nonMatchingSource))
    }

    @Test
    fun `get sources by non-empty query returns failure when repository returns failure with exception`() {
        val query = "test"
        val exception = Exception("Test Exception")
        coEvery {
            sourcesRepository.getSources(refresh = any(), language = any(), query = query)
        } returns Result.failure(exception)

        val result = runBlocking { getSourcesByQueryUseCase.invoke(query) }

        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }

    @Test
    fun `getSourcesByQueryUseCase calls sourcesRepository with correct arguments`() {
        val query = "test"
        coEvery {
            sourcesRepository.getSources(refresh = any(), language = any(), query = query)
        } returns Result.success(emptyList())

        runBlocking { getSourcesByQueryUseCase.invoke(query) }

        coVerify { sourcesRepository.getSources(refresh = false, language = null, query = query) }
    }


}


















