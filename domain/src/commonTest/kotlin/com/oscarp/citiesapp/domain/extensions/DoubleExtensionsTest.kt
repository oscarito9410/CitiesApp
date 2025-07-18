package com.oscarp.citiesapp.domain.extensions

import kotlin.test.Test
import kotlin.test.assertEquals

class DoubleExtensionsTest {

    @Test
    fun `format rounds to 0 digits`() {
        val result = 3.14159.format(0)
        assertEquals("3.0", result)
    }

    @Test
    fun `format rounds to 1 digit`() {
        val result = 3.14159.format(1)
        assertEquals("3.1", result)
    }

    @Test
    fun `format rounds to 2 digits`() {
        val result = 3.14159.format(2)
        assertEquals("3.14", result)
    }

    @Test
    fun `format rounds down correctly`() {
        val result = 3.14159.format(3)
        assertEquals("3.142", result)
    }

    @Test
    fun `format negative number`() {
        val result = (-2.71828).format(2)
        assertEquals("-2.72", result)
    }
}
