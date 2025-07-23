package com.oscarp.citiesapp.mappers

import com.oscarp.citiesapp.domain.models.City
import com.oscarp.citiesapp.navigation.CityMapDetail
import kotlin.test.Test
import kotlin.test.assertEquals

class CityMappersTest {
    @Test
    fun `toCityMapDetail correctly maps City to CityMapDetail`() {
        // given
        val city = City(
            id = 123,
            name = "Puebla",
            country = "MX",
            latitude = 19.4326,
            longitude = -99.1332,
            isFavorite = true
        )

        // when
        val result = city.toCityMapDetail()

        // then
        val expected = CityMapDetail(
            id = 123,
            name = "Puebla",
            countryCode = "MX",
            latitude = 19.4326,
            longitude = -99.1332,
            isFavorite = true
        )

        assertEquals(expected, result)
    }

    @Test
    fun `toCity correctly maps CityMapDetail to City`() {
        // given
        val detail = CityMapDetail(
            id = 456,
            name = "CDMX",
            countryCode = "MX",
            latitude = 19.4,
            longitude = -99.1,
            isFavorite = false
        )

        // when
        val result = detail.toCity()

        // then
        val expected = City(
            id = 456,
            name = "CDMX",
            country = "MX",
            latitude = 19.4,
            longitude = -99.1,
            isFavorite = false
        )

        assertEquals(expected, result)
    }
}
