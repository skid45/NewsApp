package com.skid.utils

import android.content.Context
import android.content.res.Resources.NotFoundException
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import io.mockk.verify
import junitparams.JUnitParamsRunner
import junitparams.Parameters
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(JUnitParamsRunner::class)
class ResourceWrapperTest {

    @get:Rule
    val mockKRule = MockKRule(this)

    @MockK
    lateinit var context: Context

    private lateinit var resourceWrapper: ResourceWrapper

    @Before
    fun setup() {
        resourceWrapper = ResourceWrapper(context)
    }

    @Test
    fun `getString calls with correct args and returns correct string`() {
        val stringId = 1
        val expectedString = "test string"
        every { context.getString(stringId) } returns expectedString

        val result = resourceWrapper.getString(stringId)

        verify(exactly = 1) { context.getString(stringId) }
        assertEquals(expectedString, result)
    }

    @Test(expected = NotFoundException::class)
    fun `getString throws not found exception`() {
        val stringId = 1
        every { context.getString(stringId) } throws NotFoundException()

        resourceWrapper.getString(stringId)
    }

    @Test
    @Parameters("1, test string 1", "2, test string 2", "3, test string 3")
    fun `getString calls with different resource ids and returns different strings`(
        stringId: Int,
        expectedString: String,
    ) {
        every { context.getString(stringId) } returns expectedString

        val result = resourceWrapper.getString(stringId)

        verify(exactly = 1) { context.getString(stringId) }
        assertEquals(expectedString, result)
    }
}