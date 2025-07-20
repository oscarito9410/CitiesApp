package com.oscarp.citiesapp.domain.exceptions

/**
 * Custom exception thrown when a requested city is not found in the data source.
 */
class CityNotFoundException(message: String) : Exception(message)
