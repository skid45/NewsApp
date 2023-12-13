package com.skid.filters

import com.skid.database.sources.dao.FiltersDao
import com.skid.database.sources.model.FiltersEntity
import com.skid.filters.mapper.toFilters
import com.skid.filters.mapper.toFiltersEntity
import com.skid.filters.model.Filters
import com.skid.filters.model.Language
import com.skid.filters.model.Sorting
import com.skid.filters.repository.FiltersRepositoryImpl
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import io.mockk.verify
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.Calendar

class FilterRepositoryImplTest {

    @get:Rule
    val rule = MockKRule(this)

    private lateinit var filtersRepositoryImpl: FiltersRepositoryImpl

    @MockK
    lateinit var filtersDao: FiltersDao

    private var filtersEntityInDB: FiltersEntity? = null

    @Before
    fun setup() {
        filtersRepositoryImpl = FiltersRepositoryImpl(
            filtersDao = filtersDao
        )
    }

    @After
    fun teardown() {
        filtersEntityInDB = null
    }

    @Test
    fun `getFilters with empty database returns default filters`() {
        val expectedFilters = filtersEntityInDB.toFilters()
        every { filtersDao.getFilters() } returns flowOf(filtersEntityInDB)

        val result = runBlocking { filtersRepositoryImpl.getFilters().first() }

        verify { filtersDao.getFilters() }
        assertEquals(expectedFilters, result)
    }

    @Test
    fun `getFilters with non-empty database returns filters from database`() {
        val expectedFilters = Filters(
            sortBy = Sorting.RELEVANT,
            chosenDates = Pair(
                Calendar.getInstance().apply { timeInMillis = 11101200320 },
                Calendar.getInstance().apply { timeInMillis = 11102200320 }
            ),
            language = Language.DEUTSCH,
            numberOfFilters = 3
        )
        filtersEntityInDB = expectedFilters.toFiltersEntity()
        every { filtersDao.getFilters() } returns flowOf(filtersEntityInDB)

        val result = runBlocking { filtersRepositoryImpl.getFilters().last() }

        verify { filtersDao.getFilters() }
        assertEquals(expectedFilters, result)
    }

    @Test
    fun `saveFilters saves correct filters`() {
        val filters = Filters(
            sortBy = Sorting.RELEVANT,
            chosenDates = Pair(
                Calendar.getInstance().apply { timeInMillis = 11101200320 },
                Calendar.getInstance().apply { timeInMillis = 11102200320 }
            ),
            language = Language.RUSSIAN,
            numberOfFilters = 3
        )
        coEvery {
            filtersDao.saveFilters(filters.toFiltersEntity())
        } answers { filtersEntityInDB = filters.toFiltersEntity() }

        runBlocking { filtersRepositoryImpl.saveFilters(filters) }

        coVerify { filtersDao.saveFilters(filters.toFiltersEntity()) }
        assertEquals(filters.toFiltersEntity(), filtersEntityInDB)
    }

    @Test
    fun `saveFilters updates correct filters`() {
        val oldFilters = Filters(
            sortBy = Sorting.RELEVANT,
            chosenDates = Pair(
                Calendar.getInstance().apply { timeInMillis = 11101200320 },
                Calendar.getInstance().apply { timeInMillis = 11102200320 }
            ),
            language = Language.RUSSIAN,
            numberOfFilters = 3
        )
        val newFilters = Filters(
            sortBy = Sorting.POPULAR,
            chosenDates = Pair(
                Calendar.getInstance().apply { timeInMillis = 11103200320 },
                Calendar.getInstance().apply { timeInMillis = 11104200320 }
            ),
            language = Language.ENGLISH,
            numberOfFilters = 3
        )
        coEvery {
            filtersDao.saveFilters(oldFilters.toFiltersEntity())
        } answers { filtersEntityInDB = oldFilters.toFiltersEntity() }
        coEvery {
            filtersDao.saveFilters(newFilters.toFiltersEntity())
        } answers { filtersEntityInDB = newFilters.toFiltersEntity() }

        runBlocking { filtersRepositoryImpl.saveFilters(oldFilters) }
        runBlocking { filtersRepositoryImpl.saveFilters(newFilters) }

        coVerify(exactly = 2) { filtersDao.saveFilters(any()) }
        coVerify { filtersDao.saveFilters(oldFilters.toFiltersEntity()) }
        coVerify { filtersDao.saveFilters(newFilters.toFiltersEntity()) }
        assertNotEquals(oldFilters, filtersEntityInDB.toFilters())
        assertEquals(newFilters, filtersEntityInDB.toFilters())
    }

}