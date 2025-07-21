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
}
