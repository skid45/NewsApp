package com.skid.news.repository

import com.skid.database.sources.dao.SavedArticlesDao
import com.skid.database.sources.model.SavedArticleEntity
import com.skid.news.mapper.toArticle
import com.skid.news.mapper.toSavedArticleEntity
import com.skid.news.model.Article
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.Calendar
import java.util.concurrent.TimeUnit

class SavedArticlesRepositoryImplTest {

    @get:Rule
    val rule = MockKRule(this)

    private lateinit var savedArticlesRepositoryImpl: SavedArticlesRepositoryImpl

    @MockK
    lateinit var savedArticlesDao: SavedArticlesDao

    private val savedArticlesDB = mutableListOf<SavedArticleEntity>()

    @Before
    fun setup() {
        savedArticlesRepositoryImpl = SavedArticlesRepositoryImpl(
            savedArticlesDao = savedArticlesDao
        )
    }

    @After
    fun teardown() {
        savedArticlesDB.clear()
    }

    @Test
    fun `isExists returns true`() {
        val testUrl = "testUrl"
        val savedArticleEntity = mockk<SavedArticleEntity>(relaxed = true) {
            every { url } returns testUrl
        }
        savedArticlesDB.add(savedArticleEntity)
        every { savedArticlesDao.isExists(testUrl) } returns flowOf(savedArticlesDB.any { it.url == testUrl })

        val result = runBlocking { savedArticlesRepositoryImpl.isExists(testUrl).first() }

        verify { savedArticlesDao.isExists(testUrl) }
        assertTrue(result)
    }

    @Test
    fun `isExists returns false`() {
        val testUrl = "testUrl"
        val savedArticleEntity = mockk<SavedArticleEntity>(relaxed = true)
        savedArticlesDB.add(savedArticleEntity)
        every { savedArticlesDao.isExists(testUrl) } returns flowOf(savedArticlesDB.any { it.url == testUrl })

        val result = runBlocking { savedArticlesRepositoryImpl.isExists(testUrl).first() }

        verify { savedArticlesDao.isExists(testUrl) }
        assertFalse(result)
    }

    @Test
    fun `saveArticle saves correct articles`() {
        val article: Article = mockk(relaxed = true)
        val savedArticleEntity = article.toSavedArticleEntity()
        coEvery {
            savedArticlesDao.insert(any())
        } answers { savedArticlesDB.add(savedArticleEntity) }

        runBlocking { savedArticlesRepositoryImpl.saveArticle(article) }

        coVerify { savedArticlesDao.insert(any()) }
        assertTrue(savedArticlesDB.isNotEmpty())
        assertEquals(savedArticleEntity, savedArticlesDB.first())
    }

    @Test
    fun `saveArticle saves correct articles by multiple calls`() {
        val firstArticle: Article = mockk(relaxed = true) {
            every { url } returns "test1"
        }
        val firstSavedArticleEntity = firstArticle.toSavedArticleEntity()
        val secondArticle: Article = mockk(relaxed = true) {
            every { url } returns "test2"
        }
        val secondSavedArticleEntity = secondArticle.toSavedArticleEntity()

        var insertCalls = 0
        coEvery {
            savedArticlesDao.insert(any())
        } answers {
            if (insertCalls == 0) savedArticlesDB.add(firstSavedArticleEntity)
            else savedArticlesDB.add(secondSavedArticleEntity)
            insertCalls++
        }

        runBlocking {
            savedArticlesRepositoryImpl.saveArticle(firstArticle)
            savedArticlesRepositoryImpl.saveArticle(secondArticle)
        }

        coVerify(exactly = 2) { savedArticlesDao.insert(any()) }
        assertTrue(savedArticlesDB.isNotEmpty())
        assertEquals(
            listOf(firstSavedArticleEntity, secondSavedArticleEntity),
            savedArticlesDB
        )
    }

    @Test
    fun `deleteArticleByUrl calls with empty database`() {
        val testUrl = "testUrl"
        coEvery {
            savedArticlesDao.deleteByUrl(testUrl)
        } answers {
            savedArticlesDB
                .indexOfFirst { it.url == testUrl }
                .takeIf { it >= 0 }
                ?.let { index -> savedArticlesDB.removeAt(index) }
        }

        runBlocking { savedArticlesRepositoryImpl.deleteArticleByUrl(testUrl) }

        coVerify { savedArticlesDao.deleteByUrl(testUrl) }
        assertTrue(savedArticlesDB.isEmpty())
    }

    @Test
    fun `deleteArticleByUrl calls with non-empty database and deletes the appropriate element`() {
        val testUrl = "testUrl"
        val mockSavedArticleEntityList = listOf<SavedArticleEntity>(
            mockk(relaxed = true) { every { url } returns testUrl },
            mockk(relaxed = true) { every { url } returns testUrl },
            mockk(relaxed = true) { every { url } returns "otherUrl" }
        )
        savedArticlesDB.addAll(mockSavedArticleEntityList)
        coEvery {
            savedArticlesDao.deleteByUrl(testUrl)
        } answers {
            savedArticlesDB
                .indexOfFirst { it.url == testUrl }
                .takeIf { it >= 0 }
                ?.let { index -> savedArticlesDB.removeAt(index) }
        }

        runBlocking { savedArticlesRepositoryImpl.deleteArticleByUrl(testUrl) }

        coVerify { savedArticlesDao.deleteByUrl(testUrl) }
        assertEquals(mockSavedArticleEntityList.size - 1, savedArticlesDB.size)
        assertFalse(mockSavedArticleEntityList[0] in savedArticlesDB)
        assertTrue(mockSavedArticleEntityList[1] in savedArticlesDB)
    }

    @Test
    fun `deleteOldArticles calls with empty database`() {
        val timestamp = 120L
        coEvery {
            savedArticlesDao.deleteOldArticles(timestamp)
        } answers {
            savedArticlesDB.removeIf { it.createdAt < timestamp }
        }

        runBlocking { savedArticlesRepositoryImpl.deleteOldArticles(timestamp) }

        coVerify { savedArticlesDao.deleteOldArticles(timestamp) }
        assertTrue(savedArticlesDB.isEmpty())
    }

    @Test
    fun `deleteOldArticles calls with non-empty database and deletes the appropriate element`() {
        val timestamp = 120L
        val mockSavedArticleEntityList = listOf<SavedArticleEntity>(
            mockk(relaxed = true) { every { createdAt } returns 100 },
            mockk(relaxed = true) { every { createdAt } returns 200 },
            mockk(relaxed = true) { every { createdAt } returns 50 }
        )
        savedArticlesDB.addAll(mockSavedArticleEntityList)
        coEvery {
            savedArticlesDao.deleteOldArticles(timestamp)
        } answers {
            savedArticlesDB.removeIf { it.createdAt < timestamp }
        }

        runBlocking { savedArticlesRepositoryImpl.deleteOldArticles(timestamp) }

        coVerify { savedArticlesDao.deleteOldArticles(timestamp) }
        assertNotEquals(mockSavedArticleEntityList.size, savedArticlesDB.size)
        assertFalse(mockSavedArticleEntityList[0] in savedArticlesDB)
        assertTrue(mockSavedArticleEntityList[1] in savedArticlesDB)
        assertFalse(mockSavedArticleEntityList[2] in savedArticlesDB)
    }

    @Test
    fun `getAllArticles by null chosenDates with empty database returns empty list`() {
        coEvery { savedArticlesDao.getAllArticles() } returns savedArticlesDB

        val result = runBlocking { savedArticlesRepositoryImpl.getAllArticles(null) }

        coVerify { savedArticlesDao.getAllArticles(null, null) }
        assertTrue(result.isEmpty())
    }

    @Test
    fun `getAllArticles by null chosenDates with non-empty database returns full list of articles from database`() {
        val mockSavedArticleEntityList = listOf<SavedArticleEntity>(
            mockk(relaxed = true) { every { url } returns "test1" },
            mockk(relaxed = true) { every { url } returns "test2" },
            mockk(relaxed = true) { every { url } returns "test3" }
        )
        savedArticlesDB.addAll(mockSavedArticleEntityList)
        coEvery { savedArticlesDao.getAllArticles() } returns savedArticlesDB

        val result = runBlocking { savedArticlesRepositoryImpl.getAllArticles(null) }

        coVerify { savedArticlesDao.getAllArticles(null, null) }
        assertTrue(result.isNotEmpty())
        assertEquals(
            mockSavedArticleEntityList.map(SavedArticleEntity::toArticle),
            result
        )
    }

    @Test
    fun `getAllArticles by non-null chosenDates with empty database returns empty list`() {
        val chosenDates = Pair(
            Calendar.getInstance().apply { timeInMillis = 100 },
            Calendar.getInstance().apply { timeInMillis = 200 }
        )
        coEvery {
            savedArticlesDao.getAllArticles(
                chosenDates.first.timeInMillis,
                chosenDates.second.timeInMillis.plus(TimeUnit.DAYS.toMillis(1))
            )
        } returns savedArticlesDB.filter { savedArticleEntity ->
            savedArticleEntity.publishedAt.timeInMillis in
                    chosenDates.first.timeInMillis..chosenDates.second.timeInMillis.plus(
                TimeUnit.DAYS.toMillis(
                    1
                )
            )
        }

        val result = runBlocking { savedArticlesRepositoryImpl.getAllArticles(chosenDates) }

        coVerify {
            savedArticlesDao.getAllArticles(
                chosenDates.first.timeInMillis,
                chosenDates.second.timeInMillis.plus(TimeUnit.DAYS.toMillis(1))
            )
        }
        assertTrue(result.isEmpty())
    }

    @Test
    fun `getAllArticles by non-null chosenDates with non-empty database returns full list of articles from database`() {
        val chosenDates = Pair(
            Calendar.getInstance().apply { timeInMillis = 100 },
            Calendar.getInstance().apply { timeInMillis = 200 }
        )
        val mockSavedArticleEntityList = listOf<SavedArticleEntity>(
            mockk(relaxed = true) {
                every { publishedAt } returns Calendar.getInstance().apply { timeInMillis = 20 }
            },
            mockk(relaxed = true) {
                every { publishedAt } returns Calendar.getInstance().apply { timeInMillis = 120 }
            },
            mockk(relaxed = true) {
                every { publishedAt } returns Calendar.getInstance().apply { timeInMillis = 180 }
            },
            mockk(relaxed = true) {
                every { publishedAt } returns Calendar.getInstance().apply { timeInMillis = 280 + TimeUnit.DAYS.toMillis(1) }
            }
        )
        savedArticlesDB.addAll(mockSavedArticleEntityList)
        coEvery {
            savedArticlesDao.getAllArticles(
                chosenDates.first.timeInMillis,
                chosenDates.second.timeInMillis.plus(TimeUnit.DAYS.toMillis(1))
            )
        } returns savedArticlesDB.filter { savedArticleEntity ->
            savedArticleEntity.publishedAt.timeInMillis in
                    chosenDates.first.timeInMillis..chosenDates.second.timeInMillis
                .plus(TimeUnit.DAYS.toMillis(1))
        }

        val result = runBlocking { savedArticlesRepositoryImpl.getAllArticles(chosenDates) }

        coVerify {
            savedArticlesDao.getAllArticles(
                chosenDates.first.timeInMillis,
                chosenDates.second.timeInMillis.plus(TimeUnit.DAYS.toMillis(1))
            )
        }
        assertTrue(result.isNotEmpty())
        assertEquals(
            mockSavedArticleEntityList
                .filter { savedArticleEntity ->
                    savedArticleEntity.publishedAt.timeInMillis in
                            chosenDates.first.timeInMillis..chosenDates.second.timeInMillis
                        .plus(TimeUnit.DAYS.toMillis(1))
                }
                .map(SavedArticleEntity::toArticle),
            result
        )
    }

    @Test
    fun `getArticlesByQuery by empty query with empty database returns empty list`() {
        val query = ""
        coEvery {
            savedArticlesDao.getArticlesByQuery(query)
        } returns savedArticlesDB.filter { article ->
            query in article.title || query in article.description || query in article.content
        }

        val result = runBlocking { savedArticlesRepositoryImpl.getArticlesByQuery(query) }

        coVerify { savedArticlesDao.getArticlesByQuery(query) }
        assertTrue(result.isEmpty())
    }

    @Test
    fun `getArticlesByQuery by empty query with non-empty database returns full list of articles from database`() {
        val query = ""
        val mockSavedArticleEntityList = listOf<SavedArticleEntity>(
            mockk(relaxed = true) {
                every { title } returns "test1"
                every { description } returns "test1"
                every { content } returns "test1"
            },
            mockk(relaxed = true) {
                every { title } returns "test2"
                every { description } returns "test2"
                every { content } returns "test2"
            },
            mockk(relaxed = true) {
                every { title } returns "test3"
                every { description } returns "test3"
                every { content } returns "test3"
            },
        )
        savedArticlesDB.addAll(mockSavedArticleEntityList)
        coEvery {
            savedArticlesDao.getArticlesByQuery(query)
        } returns savedArticlesDB.filter { article ->
            query in article.title || query in article.description || query in article.content
        }

        val result = runBlocking { savedArticlesRepositoryImpl.getArticlesByQuery(query) }

        coVerify { savedArticlesDao.getArticlesByQuery(query) }
        assertTrue(result.isNotEmpty())
        assertEquals(
            mockSavedArticleEntityList
                .filter { article ->
                    query in article.title || query in article.description || query in article.content
                }
                .map(SavedArticleEntity::toArticle),
            result
        )
    }

    @Test
    fun `getArticlesByQuery by non-empty query with empty database returns empty list`() {
        val query = "test"
        coEvery {
            savedArticlesDao.getArticlesByQuery(query)
        } returns savedArticlesDB.filter { article ->
            query in article.title || query in article.description || query in article.content
        }

        val result = runBlocking { savedArticlesRepositoryImpl.getArticlesByQuery(query) }

        coVerify { savedArticlesDao.getArticlesByQuery(query) }
        assertTrue(result.isEmpty())
    }

    @Test
    fun `getArticlesByQuery by non-empty query with non-empty database returns list of articles matching query from database`() {
        val query = "test"
        val mockSavedArticleEntityList = listOf<SavedArticleEntity>(
            mockk(relaxed = true) {
                every { title } returns "test1"
                every { description } returns "test1"
                every { content } returns "test1"
            },
            mockk(relaxed = true) {
                every { title } returns "other"
                every { description } returns "other"
                every { content } returns "other"
            },
            mockk(relaxed = true) {
                every { title } returns "test3"
                every { description } returns "test3"
                every { content } returns "test3"
            },
        )
        savedArticlesDB.addAll(mockSavedArticleEntityList)
        coEvery {
            savedArticlesDao.getArticlesByQuery(query)
        } returns savedArticlesDB.filter { article ->
            query in article.title || query in article.description || query in article.content
        }

        val result = runBlocking { savedArticlesRepositoryImpl.getArticlesByQuery(query) }

        coVerify { savedArticlesDao.getArticlesByQuery(query) }
        assertTrue(result.isNotEmpty())
        assertEquals(
            mockSavedArticleEntityList
                .filter { article ->
                    query in article.title || query in article.description || query in article.content
                }
                .map(SavedArticleEntity::toArticle),
            result
        )
    }
}