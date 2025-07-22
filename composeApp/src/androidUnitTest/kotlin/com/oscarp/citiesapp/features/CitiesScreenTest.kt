@file:OptIn(
    ExperimentalCoroutinesApi::class,
    ExperimentalTestApi::class
)

package com.oscarp.citiesapp.features

import android.util.Log
import androidx.compose.material3.SnackbarHostState
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import app.cash.paging.LoadStateError
import app.cash.paging.LoadStateLoading
import app.cash.paging.LoadStateNotLoading
import app.cash.paging.PagingData
import com.oscarp.citiesapp.domain.models.City
import com.oscarp.citiesapp.features.cities.AppendLoadingIndicatorTag
import com.oscarp.citiesapp.features.cities.CitiesEffect
import com.oscarp.citiesapp.features.cities.CitiesIntent
import com.oscarp.citiesapp.features.cities.CitiesScreen
import com.oscarp.citiesapp.features.cities.CitiesViewModel
import com.oscarp.citiesapp.features.cities.CitiesViewState
import com.oscarp.citiesapp.features.cities.CityMapDetailTag
import com.oscarp.citiesapp.features.cities.RefreshLoadingIndicatorTag
import com.oscarp.citiesapp.features.cities.SingleColumnCitiesListTag
import com.oscarp.citiesapp.testutil.DeviceQualifiers
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
import org.junit.runner.RunWith
import org.robolectric.ParameterizedRobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import kotlin.test.assertIs

@RunWith(ParameterizedRobolectricTestRunner::class)
class CitiesScreenTest(
    private val qualifiers: String
) : RobolectricComposeTest() {

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
        RuntimeEnvironment.setQualifiers(qualifiers)

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

            // when phone is portrait then map is not displayed
            if (qualifiers == DeviceQualifiers.PhonePortrait) {
                onNodeWithTag(CityMapDetailTag).assertDoesNotExist()
                onNodeWithTag(SingleColumnCitiesListTag).assertIsDisplayed()
            }
        }

    @Test
    fun toggle_favorite_switch_triggers_intent() = runComposeUiTest {
        testScope.runTest {
            every { viewModel.processIntent(any()) } just Runs

            pagingFlow.emit(PagingData.from(listOf(fakeCity)))

            setContent {
                AppTheme {
                    CitiesScreen(viewModel = viewModel, onCityDetailNavigation = {})
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
                CitiesScreen(
                    viewModel = viewModel,
                    onCityDetailNavigation = {}
                )
            }
        }

        waitForIdle()

        onNodeWithText("No Items").assertIsDisplayed()
    }

    @Test
    fun ui_effect_refresh_page_works_correctly() = runComposeUiTest {
        fixtureViewModel()

        testScope.runTest {
            pagingFlow.emit(PagingData.from(emptyList()))
            uiEffectFlow.emit(CitiesEffect.RefreshCitiesPagination)
        }

        setContent {
            AppTheme {
                CitiesScreen(viewModel = viewModel, onCityDetailNavigation = {})
            }
        }

        waitForIdle()

        (viewModel.uiEffect as StateFlow<CitiesEffect>).value.apply {
            assertIs<CitiesEffect.RefreshCitiesPagination>(this)
        }
    }

    @Test
    fun ui_effect_navigate_cities_detail_works_correctly() = runComposeUiTest {
        fixtureViewModel()

        testScope.runTest {
            pagingFlow.emit(
                PagingData.from(
                    listOf(
                        fakeCity
                    )
                )
            )
            uiEffectFlow.emit(CitiesEffect.NavigateToCityDetails(fakeCity))
        }

        setContent {
            AppTheme {
                CitiesScreen(viewModel = viewModel, onCityDetailNavigation = {})
            }
        }

        waitForIdle()

        (viewModel.uiEffect as StateFlow<CitiesEffect>).value.apply {
            assertIs<CitiesEffect.NavigateToCityDetails>(this)
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
                    onCityDetailNavigation = {}
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

    @Test
    fun cities_screen_displays_search_filter_bar_and_map_when_tablet() {
        if (qualifiers == DeviceQualifiers.PhonePortrait) {
            Log.d("CitiesScreenTest", "Skipping tablet test on phone portrait")
            return
        }

        runComposeUiTest(effectContext = testDispatcher) {
            testScope.runTest {
                pagingFlow.emit(PagingData.from(listOf(fakeCity)))
                stateFlow.emit(
                    CitiesViewState(
                        selectedCity = fakeCity,
                        isLoading = false
                    )
                )
            }

            setContent {
                AppTheme {
                    CitiesScreen(viewModel = viewModel)
                }
            }

            onNodeWithTag("${CityItemTag}_1").assertIsDisplayed()
            onNodeWithTag(FavoriteButtonTag).assertIsDisplayed()
            // assert the map is shown in tablet mode
            onNodeWithText(
                "Map render in test",
            ).assertIsDisplayed()
        }
    }

    @Test
    fun shows_refresh_loading_state_when_is_loading() = runComposeUiTest {
        testScope.runTest {
            pagingFlow.emit(
                PagingData.from(
                    listOf(fakeCity),
                    sourceLoadStates = app.cash.paging.LoadStates(
                        refresh = LoadStateLoading,
                        prepend = LoadStateNotLoading(
                            endOfPaginationReached = true
                        ),
                        append = LoadStateNotLoading(endOfPaginationReached = true),
                    )
                )
            )

            stateFlow.emit(CitiesViewState(isLoading = true))
        }

        setContent {
            AppTheme {
                CitiesScreen(viewModel = viewModel)
            }
        }

        onNodeWithTag(RefreshLoadingIndicatorTag).assertIsDisplayed()
    }

    @Test
    fun shows_append_loading_state_when_is_loading() = runComposeUiTest {
        testScope.runTest {
            pagingFlow.emit(
                PagingData.from(
                    listOf(fakeCity),
                    sourceLoadStates = app.cash.paging.LoadStates(
                        refresh = LoadStateNotLoading(endOfPaginationReached = true),
                        prepend = LoadStateNotLoading(endOfPaginationReached = true),
                        append = LoadStateLoading,
                    )
                )
            )

            stateFlow.emit(CitiesViewState(isLoading = true))

            setContent {
                AppTheme {
                    CitiesScreen(viewModel = viewModel)
                }
            }

            onNodeWithTag(AppendLoadingIndicatorTag).assertIsDisplayed()
        }
    }

    @Test
    fun shows_error_state_when_append() = runComposeUiTest {
        testScope.runTest {
            pagingFlow.emit(
                PagingData.from(
                    listOf(fakeCity),
                    sourceLoadStates = app.cash.paging.LoadStates(
                        refresh = LoadStateNotLoading(endOfPaginationReached = true),
                        prepend = LoadStateNotLoading(
                            endOfPaginationReached = true
                        ),
                        append = LoadStateError(Exception("IO error"))
                    )
                )
            )

            stateFlow.emit(CitiesViewState(isLoading = true))
        }

        setContent {
            AppTheme {
                CitiesScreen(viewModel = viewModel)
            }
        }

        onNodeWithText("IO error").assertIsDisplayed()
    }

    @Test
    fun shows_error_state_when_refresh() = runComposeUiTest {
        testScope.runTest {
            pagingFlow.emit(
                PagingData.from(
                    listOf(fakeCity),
                    sourceLoadStates = app.cash.paging.LoadStates(
                        refresh = LoadStateError(Exception("IO error")),
                        prepend = LoadStateNotLoading(
                            endOfPaginationReached = true
                        ),
                        append = LoadStateNotLoading(
                            endOfPaginationReached = true
                        )
                    )
                )
            )

            stateFlow.emit(CitiesViewState(isLoading = true))
        }

        setContent {
            AppTheme {
                CitiesScreen(viewModel = viewModel)
            }
        }

        onNodeWithText("IO error").assertIsDisplayed()
    }

    @Suppress("UNCHECKED_CAST")
    private fun fixtureViewModel() {
        viewModel = mockk(relaxed = true) {
            every { state } returns stateFlow
            every { uiEffect } returns uiEffectFlow as StateFlow<CitiesEffect>
            every { paginatedCities } returns pagingFlow as StateFlow<PagingData<City>>
        }
    }

    companion object {
        @JvmStatic
        @ParameterizedRobolectricTestRunner.Parameters(name = "Qualifier={0}")
        fun data() = listOf(
            DeviceQualifiers.TabletLandscape,
            DeviceQualifiers.PhonePortrait,
            DeviceQualifiers.TabletPortrait
        )
    }
}
