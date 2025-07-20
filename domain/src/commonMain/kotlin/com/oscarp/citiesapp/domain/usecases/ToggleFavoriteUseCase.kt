package com.oscarp.citiesapp.domain.usecases

import com.oscarp.citiesapp.domain.repositories.CityRepository

open class ToggleFavoriteUseCase(
    private val cityRepository: CityRepository
) {
    open suspend operator fun invoke(cityId: Long): Boolean = cityRepository.toggleFavorite(cityId)
}
