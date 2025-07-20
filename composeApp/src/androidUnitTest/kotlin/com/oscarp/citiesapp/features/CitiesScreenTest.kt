@file:OptIn(ExperimentalCoroutinesApi::class)

package com.oscarp.citiesapp.features

import androidx.compose.material3.SnackbarHostState
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import app.cash.paging.PagingData
import com.oscarp.citiesapp.domain.models.City
import com.oscarp.citiesapp.features.cities.CitiesEffect
import com.oscarp.citiesapp.features.cities.CitiesIntent
import com.oscarp.citiesapp.features.cities.CitiesScreen
import com.oscarp.citiesapp.features.cities.CitiesViewModel
import com.oscarp.citiesapp.features.cities.CitiesViewState
import com.oscarp.citiesapp.testutil.RobolectricComposeTest
import com.oscarp.citiesapp.ui.components.CityItemTag
import com.oscarp.citiesapp.ui.components.FavoriteButtonTag
import com.oscarp.citiesapp.ui.components.SearchFavoritesSwitchTag
import com.oscarp.citiesapp.ui.resourcemanager.LocalizedMessage
import com.oscarp.citiesapp.ui.theme.AppTheme
import io.mockk.Runs
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertIs

@OptIn(ExperimentalTestApi::class)
class CitiesScreenTest : RobolectricComposeTest() {

    private lateinit var viewModel: CitiesViewModel

    private val testDispatcher = UnconfinedTestDispatcher()
    private val testScope = TestScope(testDispatcher)

    private val fakeCity = City(
        id = 1L,
        name = "Test City",
        latitude = 0.0,
        longitude = 0.0,
        isFavorite = false,
        country = "MX"
    )

    private lateinit var stateFlow: MutableStateFlow<CitiesViewState>
    private lateinit var pagingFlow: MutableSharedFlow<PagingData<City>>
    private lateinit var uiEffectFlow: MutableSharedFlow<CitiesEffect>

    @Before
    override fun setup() {
        Dispatchers.setMain(testDispatcher)
        stateFlow = MutableStateFlow(CitiesViewState())
        pagingFlow = MutableStateFlow(PagingData.empty())
        uiEffectFlow = MutableStateFlow(CitiesEffect.Idle)
        fixtureViewModel()
        super.setup()
    }

    @After
    override fun after() {
        Dispatchers.resetMain()
        super.after()
    }

    @Test
    fun cities_screen_displays_search_filter_bar_and_city_item() =
        runComposeUiTest(effectContext = testDispatcher) {
            testScope.runTest {
                pagingFlow.emit(PagingData.from(listOf(fakeCity)))
            }

            setContent {
                AppTheme {
                    CitiesScreen(viewModel = viewModel)
                }
            }

            onNodeWithTag("${CityItemTag}_1").assertIsDisplayed()
            onNodeWithTag(FavoriteButtonTag).assertIsDisplayed()
        }

    @Test
    fun toggle_favorite_switch_triggers_intent() = runComposeUiTest {
        testScope.runTest {
            every { viewModel.processIntent(any()) } just Runs

            pagingFlow.emit(PagingData.from(listOf(fakeCity)))

            setContent {
                AppTheme {
                    CitiesScreen(viewModel = viewModel, onCityClicked = {})
                }
            }

            onNodeWithTag(SearchFavoritesSwitchTag).performClick()

            advanceUntilIdle()

            verify {
                viewModel.processIntent(
                    CitiesIntent.OnShowFavoritesFilter
                )
            }
        }
    }

    @Test
    fun empty_list_state_is_shown_when_no_cities() = runComposeUiTest {
        fixtureViewModel()

        testScope.runTest {
            pagingFlow.emit(PagingData.from(emptyList()))
        }

        setContent {
            AppTheme {
                CitiesScreen(viewModel = viewModel, onCityClicked = {})
            }
        }

        waitForIdle()

        onNodeWithText("No Items").assertIsDisplayed()
    }

    @Test
    fun ui_effect_refresh_page_correctly() = runComposeUiTest {
        fixtureViewModel()

        testScope.runTest {
            pagingFlow.emit(PagingData.from(emptyList()))
            uiEffectFlow.emit(CitiesEffect.RefreshCitiesPagination)
        }

        setContent {
            AppTheme {
                CitiesScreen(viewModel = viewModel, onCityClicked = {})
            }
        }

        waitForIdle()

        (viewModel.uiEffect as StateFlow<CitiesEffect>).value.apply {
            assertIs<CitiesEffect.RefreshCitiesPagination>(this)
        }
    }

    @Test
    fun ui_effect_show_snack_bar_correctly() = runComposeUiTest {
        fixtureViewModel()

        val hostState = mockk<SnackbarHostState>(relaxed = true)

        testScope.runTest {
            pagingFlow.emit(PagingData.from(emptyList()))
            uiEffectFlow.emit(
                CitiesEffect.ShowSnackBar(
                    LocalizedMessage.CityNotFound
                )
            )
        }

        setContent {
            AppTheme {
                CitiesScreen(
                    viewModel = viewModel,
                    hostState = hostState,
                    onCityClicked = {}
                )
            }
        }

        waitForIdle()

        coVerify {
            hostState.showSnackbar(
                message = any(),
                actionLabel = null
            )
        }
    }

    private fun fixtureViewModel() {
        viewModel = mockk(relaxed = true) {
            every { state } returns stateFlow
            every { uiEffect } returns uiEffectFlow as StateFlow<CitiesEffect>
            every { paginatedCities } returns pagingFlow as StateFlow<PagingData<City>>
        }
    }
}
