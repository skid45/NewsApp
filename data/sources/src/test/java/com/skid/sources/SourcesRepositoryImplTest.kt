package com.skid.sources

import com.skid.database.sources.dao.SourcesDao
import com.skid.database.sources.model.SourceEntity
import com.skid.network.model.SourceDTO
import com.skid.network.model.SourcesResponse
import com.skid.network.service.SourcesService
import com.skid.sources.mapper.toSource
import com.skid.sources.mapper.toSourceEntity
import com.skid.sources.model.Source
import com.skid.sources.repository.SourcesRepositoryImpl
import com.skid.utils.ResourceWrapper
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import retrofit2.Response

class SourcesRepositoryImplTest {

    @get:Rule
    val rule = MockKRule(this)

    private lateinit var sourcesRepositoryImpl: SourcesRepositoryImpl

    @MockK
    lateinit var resourceWrapper: ResourceWrapper

    @MockK
    lateinit var sourcesService: SourcesService

    @MockK
    lateinit var sourcesDao: SourcesDao

    private val sourcesDB = mutableListOf<SourceEntity>()

    @Before
    fun setup() {
        sourcesRepositoryImpl = SourcesRepositoryImpl(
            sourcesService = sourcesService,
            sourcesDao = sourcesDao,
            resourceWrapper = resourceWrapper
        )
        sourcesDB.clear()

        every { resourceWrapper.getString(any()) } returns "Test error string"

        coEvery { sourcesDao.deleteAll() } answers { sourcesDB.clear() }
        coEvery { sourcesDao.getAllSources() } returns sourcesDB
    }

    @After
    fun teardown() {
        sourcesDB.clear()
    }


    @Test
    fun `get sources by default params with network connection returns success with full list of sources and clear & save list to database`() {
        val mockSourcesResponse = SourcesResponse(List(3) { mockk(relaxed = true) })
        coEvery { sourcesService.getSources() } returns Response.success(mockSourcesResponse)
        coEvery {
            sourcesDao.insertAllSources(mockSourcesResponse.sources.map(SourceDTO::toSourceEntity))
        } answers { sourcesDB.addAll(mockSourcesResponse.sources.map(SourceDTO::toSourceEntity)) }

        val result = runBlocking { sourcesRepositoryImpl.getSources() }

        coVerify { sourcesService.getSources() }
        coVerify { sourcesDao.deleteAll() }
        coVerify { sourcesDao.insertAllSources(mockSourcesResponse.sources.map(SourceDTO::toSourceEntity)) }
        coVerify { sourcesDao.getAllSources() }
        assertEquals(
            mockSourcesResponse.sources.map(SourceDTO::toSourceEntity),
            sourcesDB.toList()
        )
        assertEquals(
            Result.success(mockSourcesResponse.sources.map(SourceDTO::toSource)),
            result
        )
    }

    @Test
    fun `get sources by default params without network connection and empty database returns failure result with exception`() {
        coEvery { sourcesService.getSources() } returns Response.error(404, mockk(relaxed = true))

        val result = runBlocking { sourcesRepositoryImpl.getSources() }

        coVerify { sourcesService.getSources() }
        coVerify { sourcesDao.getAllSources() }
        assertTrue(result.isFailure)
        assertEquals(
            "Test error string",
            result.exceptionOrNull()?.localizedMessage
        )
    }

    @Test
    fun `get sources by default params without network connection and non-empty database returns success with list of sources from database`() {
        val mockSourceEntityList = List(3) { mockk<SourceEntity>(relaxed = true) }
        sourcesDB.addAll(mockSourceEntityList)
        coEvery { sourcesService.getSources() } returns Response.error(404, mockk(relaxed = true))

        val result = runBlocking { sourcesRepositoryImpl.getSources() }

        coVerify { sourcesService.getSources() }
        coVerify { sourcesDao.getAllSources() }
        assertTrue(result.isSuccess)
        assertEquals(
            sourcesDB.map(SourceEntity::toSource),
            result.getOrThrow()
        )
    }

    @Test
    fun `get sources by non-null language with network connection returns success with list of sources by language`() {
        val testLanguage = "TestLanguage"
        val mockSourcesResponse = mockk<SourcesResponse>(relaxed = true) {
            every { sources } returns listOf(
                mockk(relaxed = true) { every { language } returns "TestLanguage" },
                mockk(relaxed = true) { every { language } returns "Test" },
                mockk(relaxed = true) { every { language } returns "TestLanguage" },
            )
        }
        coEvery { sourcesService.getSources() } returns Response.success(mockSourcesResponse)
        coEvery {
            sourcesDao.insertAllSources(mockSourcesResponse.sources.map(SourceDTO::toSourceEntity))
        } answers { sourcesDB.addAll(mockSourcesResponse.sources.map(SourceDTO::toSourceEntity)) }
        coEvery {
            sourcesDao.getSourcesByLanguage(testLanguage)
        } returns sourcesDB.filter { it.language == testLanguage }

        val result = runBlocking { sourcesRepositoryImpl.getSources(language = testLanguage) }

        coVerify { sourcesService.getSources() }
        coVerify { sourcesDao.deleteAll() }
        coVerify { sourcesDao.insertAllSources(mockSourcesResponse.sources.map(SourceDTO::toSourceEntity)) }
        coVerify { sourcesDao.getSourcesByLanguage(testLanguage) }
        assertTrue(result.isSuccess)
        assertEquals(
            sourcesDB.filter { it.language == testLanguage }.map(SourceEntity::toSource),
            result.getOrThrow()
        )
    }

    @Test
    fun `get sources by non-null language without network connection and empty database returns failure result with exception`() {
        val testLanguage = "TestLanguage"
        coEvery { sourcesService.getSources() } returns Response.error(404, mockk(relaxed = true))
        coEvery {
            sourcesDao.getSourcesByLanguage(testLanguage)
        } returns sourcesDB.filter { it.language == testLanguage }

        val result = runBlocking { sourcesRepositoryImpl.getSources(language = testLanguage) }

        coVerify { sourcesService.getSources() }
        coVerify { sourcesDao.getSourcesByLanguage(testLanguage) }
        assertTrue(result.isFailure)
        assertEquals(
            "Test error string",
            result.exceptionOrNull()?.localizedMessage
        )
    }

    @Test
    fun `get sources by non-null language without network connection and non-empty database returns success with list of sources by language`() {
        val testLanguage = "TestLanguage"
        val mockSourceEntityList = listOf<SourceEntity>(
            mockk(relaxed = true) { every { language } returns "TestLanguage" },
            mockk(relaxed = true) { every { language } returns "Test" },
            mockk(relaxed = true) { every { language } returns "TestLanguage" },
        )
        sourcesDB.addAll(mockSourceEntityList)
        coEvery { sourcesService.getSources() } returns Response.error(404, mockk(relaxed = true))
        coEvery {
            sourcesDao.getSourcesByLanguage(testLanguage)
        } returns sourcesDB.filter { it.language == testLanguage }

        val result = runBlocking { sourcesRepositoryImpl.getSources(language = testLanguage) }

        coVerify { sourcesService.getSources() }
        coVerify { sourcesDao.getSourcesByLanguage(testLanguage) }
        assertTrue(result.isSuccess)
        assertEquals(
            sourcesDB.filter { it.language == testLanguage }.map(SourceEntity::toSource),
            result.getOrThrow()
        )
    }

    @Test
    fun `get sources by non-null query with network connection returns success with list of sources matching query from database`() {
        val query = "test"
        val mockSourcesResponse = mockk<SourcesResponse>(relaxed = true) {
            every { sources } returns listOf(
                mockk(relaxed = true) { every { name } returns "test1" },
                mockk(relaxed = true) { every { name } returns "other1" },
                mockk(relaxed = true) { every { name } returns "test2" },
                mockk(relaxed = true) { every { name } returns "other2" },
            )
        }
        coEvery { sourcesService.getSources() } returns Response.success(mockSourcesResponse)
        coEvery {
            sourcesDao.insertAllSources(mockSourcesResponse.sources.map(SourceDTO::toSourceEntity))
        } answers { sourcesDB.addAll(mockSourcesResponse.sources.map(SourceDTO::toSourceEntity)) }
        coEvery {
            sourcesDao.getSourcesByQuery(query)
        } returns sourcesDB.filter { it.name.contains(query, ignoreCase = true) }

        val result = runBlocking { sourcesRepositoryImpl.getSources(query = query) }

        coVerify { sourcesService.getSources() }
        coVerify { sourcesDao.deleteAll() }
        coVerify { sourcesDao.insertAllSources(mockSourcesResponse.sources.map(SourceDTO::toSourceEntity)) }
        coVerify { sourcesDao.getSourcesByQuery(query) }
        assertTrue(result.isSuccess)
        assertEquals(
            sourcesDB
                .filter { it.name.contains(query, ignoreCase = true) }
                .map(SourceEntity::toSource),
            result.getOrThrow()
        )
    }

    @Test
    fun `get sources by non-null query without network connection and empty database returns failure result with exception`() {
        val query = "test"
        coEvery { sourcesService.getSources() } returns Response.error(404, mockk(relaxed = true))
        coEvery {
            sourcesDao.getSourcesByQuery(query)
        } returns sourcesDB.filter { it.name.contains(query, ignoreCase = true) }

        val result = runBlocking { sourcesRepositoryImpl.getSources(query = query) }

        coVerify { sourcesService.getSources() }
        coVerify { sourcesDao.getSourcesByQuery(query) }
        assertTrue(result.isFailure)
        assertEquals(
            "Test error string",
            result.exceptionOrNull()?.localizedMessage
        )
    }

    @Test
    fun `get sources by non-null query without network connection and non-empty database returns success with list of sources matching query from database`() {
        val query = "test"
        val mockSourceEntityList = listOf<SourceEntity>(
            mockk(relaxed = true) { every { name } returns "test1" },
            mockk(relaxed = true) { every { name } returns "other1" },
            mockk(relaxed = true) { every { name } returns "test2" },
            mockk(relaxed = true) { every { name } returns "other2" },
        )
        sourcesDB.addAll(mockSourceEntityList)
        coEvery { sourcesService.getSources() } returns Response.error(404, mockk(relaxed = true))
        coEvery {
            sourcesDao.getSourcesByQuery(query)
        } returns sourcesDB.filter { it.name.contains(query, ignoreCase = true) }

        val result = runBlocking { sourcesRepositoryImpl.getSources(query = query) }

        coVerify { sourcesService.getSources() }
        coVerify { sourcesDao.getSourcesByQuery(query) }
        assertTrue(result.isSuccess)
        assertEquals(
            sourcesDB
                .filter { it.name.contains(query, ignoreCase = true) }
                .map(SourceEntity::toSource),
            result.getOrThrow()
        )
    }

    @Test
    fun `get sources by non-null query, refresh = false with empty database returns success with empty list`() {
        val query = "test"
        coEvery {
            sourcesDao.getSourcesByQuery(query)
        } returns sourcesDB.filter { it.name.contains(query, ignoreCase = true) }

        val result = runBlocking {
            sourcesRepositoryImpl.getSources(refresh = false, query = query)
        }

        coVerify(inverse = true) { sourcesService.getSources() }
        coVerify(exactly = 1) { sourcesDao.getSourcesByQuery(query) }
        assertTrue(result.isSuccess)
        assertEquals(
            emptyList<Source>(),
            result.getOrThrow()
        )
    }

    @Test
    fun `get sources by non-null query, refresh = false with non-empty database returns success with list of sources matching query from database`() {
        val query = "test"
        val mockSourceEntityList = listOf<SourceEntity>(
            mockk(relaxed = true) { every { name } returns "test1" },
            mockk(relaxed = true) { every { name } returns "other1" },
            mockk(relaxed = true) { every { name } returns "test2" },
            mockk(relaxed = true) { every { name } returns "other2" },
        )
        sourcesDB.addAll(mockSourceEntityList)
        coEvery {
            sourcesDao.getSourcesByQuery(query)
        } returns sourcesDB.filter { it.name.contains(query, ignoreCase = true) }

        val result = runBlocking {
            sourcesRepositoryImpl.getSources(refresh = false, query = query)
        }

        coVerify(inverse = true) { sourcesService.getSources() }
        coVerify(exactly = 1) { sourcesDao.getSourcesByQuery(query) }
        assertTrue(result.isSuccess)
        assertEquals(
            sourcesDB
                .filter { it.name.contains(query, ignoreCase = true) }
                .map(SourceEntity::toSource),
            result.getOrThrow()
        )
    }

    @Test
    fun `get sources by non-null language, refresh = false with empty database returns success with empty list`() {
        val testLanguage = "TestLanguage"
        coEvery {
            sourcesDao.getSourcesByLanguage(testLanguage)
        } returns sourcesDB.filter { it.language == testLanguage }

        val result = runBlocking {
            sourcesRepositoryImpl.getSources(refresh = false, language = testLanguage)
        }

        coVerify(inverse = true) { sourcesService.getSources() }
        coVerify(exactly = 1) { sourcesDao.getSourcesByLanguage(testLanguage) }
        assertTrue(result.isSuccess)
        assertEquals(
            emptyList<Source>(),
            result.getOrThrow()
        )
    }

    @Test
    fun `get sources by non-null language, refresh = false with non-empty database returns success with list of sources by language from database`() {
        val testLanguage = "TestLanguage"
        val mockSourceEntityList = listOf<SourceEntity>(
            mockk(relaxed = true) { every { language } returns "TestLanguage" },
            mockk(relaxed = true) { every { language } returns "Test" },
            mockk(relaxed = true) { every { language } returns "TestLanguage" },
        )
        sourcesDB.addAll(mockSourceEntityList)
        coEvery {
            sourcesDao.getSourcesByLanguage(testLanguage)
        } returns sourcesDB.filter { it.language == testLanguage }

        val result = runBlocking {
            sourcesRepositoryImpl.getSources(refresh = false, language = testLanguage)
        }

        coVerify(inverse = true) { sourcesService.getSources() }
        coVerify(exactly = 1) { sourcesDao.getSourcesByLanguage(testLanguage) }
        assertTrue(result.isSuccess)
        assertEquals(
            sourcesDB.filter { it.language == testLanguage }.map(SourceEntity::toSource),
            result.getOrThrow()
        )
    }
}