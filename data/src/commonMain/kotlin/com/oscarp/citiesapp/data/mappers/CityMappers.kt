package com.oscarp.citiesapp.data.mappers

import com.oscarp.citiesapp.data.local.entities.CityEntity
import com.oscarp.citiesapp.data.remote.CityDto
import com.oscarp.citiesapp.domain.models.City

fun CityDto.mapEntity(): CityEntity = CityEntity(
    id = id,
    name = name,
    country = country,
    latitude = coord.lat,
    longitude = coord.lon,
)

fun CityEntity.toDomain(): City = City(
    id = id,
    name = name,
    country = country,
    latitude = latitude,
    longitude = longitude,
    isFavorite = isFavorite
)
