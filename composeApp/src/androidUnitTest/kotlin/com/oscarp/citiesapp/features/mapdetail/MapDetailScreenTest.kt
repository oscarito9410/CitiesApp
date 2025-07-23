@file:OptIn(ExperimentalTestApi::class)

package com.oscarp.citiesapp.features.mapdetail

import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runComposeUiTest
import com.oscarp.citiesapp.domain.models.City
import com.oscarp.citiesapp.robot.MapDetailScreenRobot
import com.oscarp.citiesapp.testutil.RobolectricComposeTest
import io.mockk.mockk
import kotlin.test.Test

class MapDetailScreenTest : RobolectricComposeTest() {

    private val fakeCity = City(
        id = 1L,
        name = "Test City",
        latitude = 0.0,
        longitude = 0.0,
        isFavorite = false,
        country = "MX"
    )

    @Test
    fun map_detail_screen_robot_asserts_are_correct() = runComposeUiTest {
        val mockCoordinator = mockk<MapDetailCoordinator>(relaxed = true)
        val robot = mapDetailScreenRobot(fakeCity, mockCoordinator)

        with(robot) {
            setScreenContent()
            assertCityNameDisplayed()
            assertBackButtonDisplayed()
            assertMapDisplayed()
        }
    }

    fun ComposeUiTest.mapDetailScreenRobot(
        city: City,
        coordinator: MapDetailCoordinator
    ): MapDetailScreenRobot = MapDetailScreenRobot(this, coordinator, city)
}
