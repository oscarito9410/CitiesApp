package com.oscarp.citiesapp.data.local.dao

import androidx.room.*
import com.oscarp.citiesapp.data.local.entities.CityEntity

@Dao
interface CityDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCity(city: CityEntity)
}
