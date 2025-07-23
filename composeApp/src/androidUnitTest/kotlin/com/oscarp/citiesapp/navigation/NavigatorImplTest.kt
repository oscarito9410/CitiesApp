package com.oscarp.citiesapp.navigation

import androidx.navigation.NavGraph
import androidx.navigation.NavHostController
import androidx.navigation.NavOptionsBuilder
import com.oscarp.citiesapp.domain.models.City
import com.oscarp.citiesapp.mappers.toCityMapDetail
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.After
import org.junit.Before
import org.junit.Test

class NavigatorImplTest {

    private lateinit var navController: NavHostController
    private lateinit var navigator: Navigator

    private val city = City(
        id = 1L,
        name = "Test City",
        latitude = 10.0,
        longitude = 20.0,
        isFavorite = false,
        country = "MX"
    )

    @Before
    fun setUp() {
        navController = mockk(relaxed = true)
        navigator = NavigatorImpl(navController)
    }

    @After
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun `navigateToCityDetails calls navController with city detail route`() {
        // when
        navigator.navigateToCityDetails(city)

        // then
        verify { navController.navigate(city.toCityMapDetail()) }
    }

    @Test
    fun `popBackStack calls navController popBackStack`() {
        // when
        navigator.popBackStack()

        // then
        verify { navController.popBackStack() }
    }

    @Test
    fun `navigateToSyncScreen navigates with popUpTo start destination inclusive`() {
        // given
        val graph = mockk<NavGraph> {
            every { startDestinationId } returns 42
        }
        every { navController.graph } returns graph

        // when
        navigator.navigateToSyncScreen()

        // then
        verify {
            navController.navigate(
                SyncCitiesDestination,
                any<NavOptionsBuilder.() -> Unit>()
            )
        }
    }

    @Test
    fun `navigateToCitiesScreen navigates with popUpTo start destination inclusive`() {
        // given
        val graph = mockk<NavGraph> {
            every { startDestinationId } returns 77
        }
        every { navController.graph } returns graph

        // when
        navigator.navigateToCitiesScreen()

        // then
        verify {
            navController.navigate(
                CitiesDestination,
                any<NavOptionsBuilder.() -> Unit>()
            )
        }
    }
}
