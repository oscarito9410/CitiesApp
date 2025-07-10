package com.oscarp.citiesapp.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Fts4
import androidx.room.PrimaryKey

@Fts4(contentEntity = CityEntity::class)
@Entity(tableName = "cities_fts")
data class CityFtsEntity(
    @PrimaryKey
    @ColumnInfo(name = "rowid")
    val rowid: Int,
    val name: String,
    val country: String
)
