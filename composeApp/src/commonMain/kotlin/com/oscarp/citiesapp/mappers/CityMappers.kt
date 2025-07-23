package com.oscarp.citiesapp.mappers

import com.oscarp.citiesapp.domain.models.City
import com.oscarp.citiesapp.navigation.CityMapDetail

fun City.toCityMapDetail(): CityMapDetail = CityMapDetail(
    id = id,
    name = name,
    countryCode = country,
    latitude = latitude,
    longitude = longitude,
    isFavorite = isFavorite
)

fun CityMapDetail.toCity(): City = City(
    id = id,
    name = name,
    country = countryCode,
    latitude = latitude,
    longitude = longitude,
    isFavorite = isFavorite
)
