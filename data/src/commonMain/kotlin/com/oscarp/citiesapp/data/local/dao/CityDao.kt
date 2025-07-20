package com.oscarp.citiesapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.oscarp.citiesapp.data.local.entities.CityEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CityDao {

    @Query("SELECT COUNT(id) FROM cities")
    suspend fun getCitiesCount(): Int

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCities(cities: List<CityEntity>)

    @Query("SELECT * FROM cities WHERE id = :cityId")
    suspend fun getCityById(cityId: Long): CityEntity?

    @Query("UPDATE cities SET isFavorite = :isFavorite WHERE id = :cityId")
    suspend fun updateFavoriteStatus(cityId: Long, isFavorite: Boolean): Int

    @Query(
        """
        SELECT * FROM cities
        WHERE (:onlyFavorites = 0 OR isFavorite = 1)
        ORDER BY name
        LIMIT :loadSize OFFSET :offset
    """
    )
    suspend fun getPaginatedCitiesNoSearch(
        onlyFavorites: Boolean,
        loadSize: Int,
        offset: Int
    ): List<CityEntity>

    @Query(
        """
    SELECT c.*
    FROM cities AS c
    JOIN cities_fts AS fts ON c.id = fts.rowid
    WHERE (:onlyFavorites = 0 OR c.isFavorite = 1)
      AND cities_fts MATCH 'name:' || :query || '* OR country:' || :query || '*'
      AND (c.name LIKE :query || '%' COLLATE NOCASE OR c.country LIKE :query || '%' COLLATE NOCASE)
    ORDER BY c.name
    LIMIT :loadSize OFFSET :offset
"""
    )
    suspend fun getPaginatedCitiesWithSearch(
        query: String,
        onlyFavorites: Boolean,
        loadSize: Int,
        offset: Int
    ): List<CityEntity>

    @Query("SELECT id FROM cities WHERE isFavorite = 1")
    fun getFavoriteCitiesIdsFlow(): Flow<List<Long>>
}
