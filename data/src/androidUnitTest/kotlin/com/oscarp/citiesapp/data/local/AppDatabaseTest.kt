package com.oscarp.citiesapp.data.local

import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.test.platform.app.InstrumentationRegistry
import com.oscarp.citiesapp.data.local.dao.CityDao
import com.oscarp.citiesapp.data.local.entities.CityEntity
import kotlinx.coroutines.test.runTest
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.intArrayOf
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue


@Config(
    sdk = [34]
)
@RunWith(RobolectricTestRunner::class)
class AppDatabaseTest {

    fun getInMemoryDatabaseBuilder(): RoomDatabase.Builder<AppDatabase> {
        return Room.inMemoryDatabaseBuilder(
            InstrumentationRegistry.getInstrumentation().context,
            AppDatabase::class.java,
        ).allowMainThreadQueries()
    }

    private lateinit var db: AppDatabase
    private lateinit var cityDao: CityDao

    @BeforeTest // Use @BeforeEach from JUnit Jupiter
    fun setup() = runTest { // runTest for suspend functions in setup
        db = getInMemoryDatabaseBuilder().build()
        cityDao = db.cityDao()
        insertDefaultCities()
    }

    private fun insertDefaultCities() = runTest {
        val cities = listOf(
            CityEntity(
                id = 1,
                name = "Albuquerque",
                country = "US",
                isFavorite = false,
                latitude = 0.0,
                longitude = 0.0
            ),
            CityEntity(
                id = 2,
                name = "Alabama",
                country = "US",
                isFavorite = false,
                latitude = 0.0,
                longitude = 0.0
            ),
            CityEntity(
                id = 3,
                name = "London",
                country = "GB",
                isFavorite = false,
                latitude = 0.0,
                longitude = 0.0
            ),
            CityEntity(
                id = 4,
                name = "Sydney",
                country = "AU",
                isFavorite = false,
                latitude = 0.0,
                longitude = 0.0
            ),
            CityEntity(
                id = 5,
                name = "Paris",
                country = "FR",
                isFavorite = true,
                latitude = 0.0,
                longitude = 0.0
            ), // Favorite
            CityEntity(
                id = 6,
                name = "Madrid del Alba",
                country = "ES",
                isFavorite = false,
                latitude = 0.0,
                longitude = 0.0
            ),
            CityEntity(
                id = 7,
                name = "Albany",
                country = "US",
                isFavorite = false,
                latitude = 0.0,
                longitude = 0.0
            )
        )
        cityDao.insertCities(cities)
    }

    @AfterTest
    fun teardown() = runTest {
        db.clearAllTables() // Clears all data
        db.close()
    }

    @Test
    fun testSearch_strictPrefixMatching_A() = runTest {
        // "A" matches "Alabama, US", "Albuquerque, US", "Albany"
        // Should NOT match "Sydney, AU" or "Madrid del Alba"
        val results = cityDao.getPaginatedCitiesWithSearch(
            query = "Alb",
            onlyFavorites = false,
            loadSize = 10,
            offset = 0
        )
        assertEquals(2, results.size, "Expected 3 cities starting with 'A'")
        assertTrue(results.any { it.name == "Albuquerque" }, "Albuquerque should be found")
        assertTrue(results.any { it.name == "Albany" }, "Albany should be found")
        assertFalse(results.any { it.name == "Sydney" }, "Sydney should NOT be found")
        assertFalse(
            results.any { it.name == "Madrid del Alba" },
            "Madrid del Alba should NOT be found"
        )
    }

    @Test
    fun testSearch_strictPrefixMatching_s_caseInsensitive() = runTest {
        // "s" matches "Sydney, AU" (case insensitive)
        val results = cityDao.getPaginatedCitiesWithSearch(
            query = "s",
            onlyFavorites = false,
            loadSize = 10,
            offset = 0
        )
        assertEquals(1, results.size, "Expected 1 city starting with 's'")
        assertTrue(results.any { it.name == "Sydney" }, "Sydney should be found")
        assertFalse(results.any { it.name == "Paris" }, "Paris should NOT be found")
    }

    @Test
    fun testSearch_strictPrefixMatching_Al() = runTest {
        // "Al" matches "Alabama, US", "Albuquerque, US", "Albany"
        val results = cityDao.getPaginatedCitiesWithSearch(
            query = "Al",
            onlyFavorites = false,
            loadSize = 10,
            offset = 0
        )
        assertEquals(3, results.size, "Expected 3 cities starting with 'Al'")
        assertTrue(results.any { it.name == "Albuquerque" }, "Albuquerque should be found")
        assertTrue(results.any { it.name == "Alabama" }, "Alabama should be found")
        assertTrue(results.any { it.name == "Albany" }, "Albany should be found")
        assertFalse(
            results.any { it.name == "Madrid del Alba" },
            "Madrid del Alba should NOT be found"
        )
    }

    @Test
    fun testSearch_strictPrefixMatching_Alb() = runTest {
        // "Alb" matches only "Albuquerque, US" and "Albany"
        // Crucially, it should NOT match "Madrid del Alba"
        val results = cityDao.getPaginatedCitiesWithSearch(
            query = "Alb",
            onlyFavorites = false,
            loadSize = 10,
            offset = 0
        )
        assertEquals(2, results.size, "Expected 2 cities starting with 'Alb'")
        assertTrue(results.any { it.name == "Albuquerque" }, "Albuquerque should be found")
        assertTrue(results.any { it.name == "Albany" }, "Albany should be found")
        assertFalse(
            results.any { it.name == "Madrid del Alba" },
            "Madrid del Alba should NOT be found"
        )
    }

    @Test
    fun testSearch_onlyFavorites() = runTest {
        val results = cityDao.getPaginatedCitiesWithSearch(
            query = "P", // "P" for Paris
            onlyFavorites = true,
            loadSize = 10,
            offset = 0
        )
        assertEquals(1, results.size, "Expected 1 favorite city starting with 'P'")
        assertTrue(results.any { it.name == "Paris" }, "Paris should be found")
    }

    @Test
    fun testSearch_noMatch() = runTest {
        val results = cityDao.getPaginatedCitiesWithSearch(
            query = "XYZ",
            onlyFavorites = false,
            loadSize = 10,
            offset = 0
        )
        assertTrue(results.isEmpty(), "Expected no results for 'XYZ'")
    }

    @Test
    fun testSearch_emptyQuery() = runTest {
        insertDefaultCities()
        val results = cityDao.getPaginatedCitiesNoSearch(
            onlyFavorites = false,
            loadSize = 10,
            offset = 0
        )
        assertEquals(7, results.size, "Expected all 7 cities for an empty query")
    }

    @Test
    fun testSearch_withPagination_offsetAndLimit() = runTest {
        val allCities = listOf(
            CityEntity(
                id = 10,
                name = "CityA",
                country = "C1",
                isFavorite = false,
                latitude = 0.0,
                longitude = 0.0
            ),
            CityEntity(
                id = 11,
                name = "CityB",
                country = "C1",
                isFavorite = false,
                latitude = 0.0,
                longitude = 0.0
            ),
            CityEntity(
                id = 12,
                name = "CityC",
                country = "C1",
                isFavorite = false,
                latitude = 0.0,
                longitude = 0.0
            ),
            CityEntity(
                id = 13,
                name = "CityD",
                country = "C1",
                isFavorite = false,
                latitude = 0.0,
                longitude = 0.0
            ),
            CityEntity(
                id = 14,
                name = "CityE",
                country = "C1",
                isFavorite = false,
                latitude = 0.0,
                longitude = 0.0
            ),
            CityEntity(
                id = 15,
                name = "CityF",
                country = "C1",
                isFavorite = false,
                latitude = 0.0,
                longitude = 0.0
            )
        )
        cityDao.insertCities(allCities) // Add more cities to test pagination

        val resultsPage1 = cityDao.getPaginatedCitiesNoSearch(
            onlyFavorites = false,
            loadSize = 3,
            offset = 0
        )
        assertEquals(3, resultsPage1.size)
        // Check order as per ORDER BY c.name
        assertEquals("Alabama", resultsPage1[0].name)
        assertEquals("Albany", resultsPage1[1].name)
        assertEquals("Albuquerque", resultsPage1[2].name)


        val resultsPage2 = cityDao.getPaginatedCitiesNoSearch(
            onlyFavorites = false,
            loadSize = 3,
            offset = 3
        )
        assertEquals(3, resultsPage2.size)
        assertEquals("CityA", resultsPage2[0].name)
        assertEquals("CityB", resultsPage2[1].name)
        assertEquals("CityC", resultsPage2[2].name)
    }

    @Test
    fun testCitiesCount() = runTest {
        insertDefaultCities()
        val resultCount = cityDao.getCitiesCount()
        assertEquals(7, resultCount, "Expected all 7 cities for an count query")
    }
}