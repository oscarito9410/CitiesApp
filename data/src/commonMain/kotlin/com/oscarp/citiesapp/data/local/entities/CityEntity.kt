package com.oscarp.citiesapp.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cities")
data class CityEntity(
    @PrimaryKey val id: String,
    val name: String,
    val country: String,
    val latitude: Double,
    val longitude: Double,
    val isFavorite: Boolean = false,
)
