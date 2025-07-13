package com.oscarp.citiesapp.data.mappers

import com.oscarp.citiesapp.data.local.entities.CityEntity
import com.oscarp.citiesapp.data.remote.CityDto
import com.oscarp.citiesapp.data.remote.CoordDto
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class CityMapperTest {

    @Test
    fun `given a CityDto then maps correctly to entity`() = runTest {
        // given
        val dto = CityDto(
            id = 42L,
            name = "Gotham",
            country = "US",
            coord = CoordDto(lat = 12.34, lon = 56.78)
        )

        // when
        val entity = dto.mapEntity()

        // then
        assertEquals(42L, entity.id)
        assertEquals("Gotham", entity.name)
        assertEquals("US", entity.country)
        assertEquals(12.34, entity.latitude)
        assertEquals(56.78, entity.longitude)
        // default favorite should be false
        assertEquals(false, entity.isFavorite)
    }

    @Test
    fun `given a CityEntity the maps correctly to domain`() {
        // given
        val entity = CityEntity(
            id = 99L,
            name = "Metropolis",
            country = "US",
            latitude = 98.76,
            longitude = 54.32,
            isFavorite = true
        )

        // when
        val domain = entity.toDomain()

        // then
        assertEquals(99L, domain.id)
        assertEquals("Metropolis", domain.name)
        assertEquals("US", domain.country)
        assertEquals(98.76, domain.latitude)
        assertEquals(54.32, domain.longitude)
    }
}
