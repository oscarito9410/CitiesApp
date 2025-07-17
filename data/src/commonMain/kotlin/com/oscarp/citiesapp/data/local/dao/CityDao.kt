package com.oscarp.citiesapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.oscarp.citiesapp.data.local.entities.CityEntity

@Dao
interface CityDao {

    @Query("SELECT COUNT(id) FROM cities")
    suspend fun getCitiesCount(): Int

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCities(cities: List<CityEntity>)

    @Query(
        """
  SELECT c.* 
  FROM cities AS c
  JOIN cities_fts AS fts ON c.id = fts.rowid
  WHERE cities_fts MATCH 'name:' || :query || '* OR country:' || :query || '*'
  ORDER BY c.name
"""
    )
    suspend fun searchCitiesExplicit(query: String): List<CityEntity>
}
