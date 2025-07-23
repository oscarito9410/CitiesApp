@file:OptIn(ExperimentalCoroutinesApi::class, ExperimentalTestApi::class)

package com.oscarp.citiesapp.features.cities

import androidx.compose.material3.SnackbarHostState
import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runComposeUiTest
import app.cash.paging.LoadStateError
import app.cash.paging.LoadStateLoading
import app.cash.paging.LoadStateNotLoading
import app.cash.paging.LoadStates
import app.cash.paging.PagingData
import com.oscarp.citiesapp.domain.models.City
import com.oscarp.citiesapp.robot.CitiesScreenRobot
import com.oscarp.citiesapp.testutil.DeviceQualifiers
import com.oscarp.citiesapp.testutil.RobolectricComposeTest
import com.oscarp.citiesapp.ui.resourcemanager.LocalizedMessage
import io.mockk.Runs
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.runner.RunWith
import org.robolectric.ParameterizedRobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import kotlin.test.Test
import kotlin.test.assertIs

@RunWith(ParameterizedRobolectricTestRunner::class)
class CitiesScreenTest(
    private val qualifiers: String
) : RobolectricComposeTest() {

    private lateinit var viewModel: CitiesViewModel
    private lateinit var coodinator: CitiesCoordinator
    private val hostState: SnackbarHostState = mockk(relaxed = true)

    private val testDispatcher = UnconfinedTestDispatcher()

    private val fakeCity = City(
        id = 1L,
        name = "Test City",
        latitude = 0.0,
        longitude = 0.0,
        isFavorite = false,
        country = "MX"
    )

    private lateinit var stateFlow: MutableStateFlow<CitiesViewState>
    private lateinit var pagingFlow: MutableStateFlow<PagingData<City>>
    private lateinit var uiEffectFlow: MutableStateFlow<CitiesEffect>

    @Before
    override fun setup() {
        Dispatchers.setMain(testDispatcher)
        RuntimeEnvironment.setQualifiers(qualifiers)

        stateFlow = MutableStateFlow(CitiesViewState())
        pagingFlow = MutableStateFlow(PagingData.empty())
        uiEffectFlow = MutableStateFlow(CitiesEffect.Idle)
        fixtureViewModel()
        fixtureCoordinator()
        super.setup()
    }

    @After
    override fun after() {
        Dispatchers.resetMain()
        super.after()
    }

    @Test
    fun cities_screen_displays_search_filter_and_enter_texts() =
        runComposeUiTest(effectContext = testDispatcher) {
            runTest {
                emitSuccessState(fakeCity)
            }

            val robot = citiesScreenRobot()

            with(robot) {
                setScreenContent()
                assertCityItemDisplayed(fakeCity.id)
                assertFavoriteButtonDisplayed()
                assertSearchTextFieldDisplayed()
                enterSearchQuery("Puebla")
            }

            if (qualifiers == DeviceQualifiers.PhonePortrait) {
                robot.assertCityMapDetailNotDisplayed()
                robot.assertSingleColumnListDisplayed()
            }
        }

    @Test
    fun toggle_favorite_switch_triggers_coordinator_action() = runComposeUiTest {
        every { viewModel.processIntent(any()) } just Runs
        runTest {
            emitSuccessState(fakeCity)
        }

        val robot = citiesScreenRobot()
        with(robot) {
            setScreenContent()
            toggleFavoritesSwitch()
        }

        verify {
            coodinator.onShowFavoritesFilterToggled(any())
        }
    }

    @Test
    fun empty_list_state_is_shown_when_no_cities() = runComposeUiTest {
        runTest {
            emitEmptyState()
        }
        val robot = citiesScreenRobot()
        robot.setScreenContent()
        robot.assertEmptyCitiesListDisplayed()
    }

    @Test
    fun ui_effect_refresh_page_works_correctly() = runComposeUiTest {
        runTest {
            emitEmptyState()
        }
        emitEffect(CitiesEffect.RefreshCitiesPagination)

        val robot = citiesScreenRobot()
        robot.setScreenContent()

        assertIs<CitiesEffect.RefreshCitiesPagination>(
            (viewModel.uiEffect as StateFlow<CitiesEffect>).value
        )
    }

    @Test
    fun ui_effect_show_snack_bar_correctly() = runComposeUiTest {
        runTest {
            emitEmptyState()
        }

        emitEffect(CitiesEffect.ShowSnackBar(LocalizedMessage.CityNotFound))

        val robot = citiesScreenRobot()

        robot.setScreenContent()

        coVerify {
            hostState.showSnackbar(
                message = any(),
                actionLabel = null
            )
        }
    }

    @Test
    fun cities_screen_displays_search_filter_bar_and_map_when_tablet() =
        runComposeUiTest(effectContext = testDispatcher) {
            if (qualifiers == DeviceQualifiers.PhonePortrait) return@runComposeUiTest

            runTest {
                emitSelectedCityState(fakeCity)
            }

            val robot = citiesScreenRobot()
            with(robot) {
                setScreenContent()
                assertCityItemDisplayed(fakeCity.id)
                clickFavoriteButton()
                assertMapRenderTextDisplayed()
            }
        }

    @Test
    fun cities_screen_displays_search_filter_bar_and_empty_state_when_tablet() =
        runComposeUiTest(effectContext = testDispatcher) {
            if (qualifiers == DeviceQualifiers.PhonePortrait) {
                return@runComposeUiTest
            }

            runTest {
                emitUnselectedCityState(fakeCity)
                advanceUntilIdle()
            }

            val robot = citiesScreenRobot()
            with(robot) {
                setScreenContent()
                assertCityItemDisplayed(fakeCity.id)
                clickFavoriteButton()
                assertEmptyCitySelectedTextDisplayed()
            }
        }

    @Test
    fun cities_screen_displays_search_filter_bar_and_taps_city() =
        runComposeUiTest(effectContext = testDispatcher) {
            runTest {
                emitSelectedCityState(fakeCity)
            }

            val robot = citiesScreenRobot()

            with(robot) {
                setScreenContent()
                clickCityItem(fakeCity.id)
                clickFavoriteButton()
            }

            verify {
                coodinator.onCitySelected(fakeCity, any())
            }
        }

    @Test
    fun cities_screen_displays_search_filter_bar_and_taps_city_when_tablet() =
        runComposeUiTest(effectContext = testDispatcher) {
            if (qualifiers == DeviceQualifiers.PhonePortrait) {
                return@runComposeUiTest
            }

            runTest {
                emitSelectedCityState(fakeCity)
            }

            val robot = citiesScreenRobot()

            with(robot) {
                setScreenContent()
                clickCityItem(fakeCity.id)
                assertMapRenderTextDisplayed()
            }

            verify {
                coodinator.onCitySelected(fakeCity, any())
            }
        }

    @Test
    fun shows_refresh_loading_state_when_is_loading() = runComposeUiTest {
        emitLoadingRefreshState(fakeCity)

        val robot = citiesScreenRobot()

        with(robot) {
            setScreenContent()
            assertRefreshLoadingIndicatorDisplayed()
        }
    }

    @Test
    fun shows_append_loading_state_when_is_loading() = runComposeUiTest {
        emitAppendLoadingState(fakeCity)

        val robot = citiesScreenRobot()

        with(robot) {
            setScreenContent()
            assertAppendLoadingIndicatorDisplayed()
        }
    }

    @Test
    fun shows_error_state_when_append() = runComposeUiTest {
        emitAppendErrorState(fakeCity)

        val robot = citiesScreenRobot()

        with(robot) {
            setScreenContent()
            assertGenericErrorDisplayed()
        }
    }

    @Test
    fun shows_error_state_when_refresh() = runComposeUiTest {
        emitRefreshErrorState(fakeCity)

        val robot = citiesScreenRobot()
        with(robot) {
            setScreenContent()
            assertErrorTextDisplayed("Generic error")
        }
    }

    // -------------------- Helpers ------------------------

    fun ComposeUiTest.citiesScreenRobot(): CitiesScreenRobot =
        CitiesScreenRobot(this, viewModel, coodinator, hostState)

    private suspend fun emitSuccessState(vararg cities: City) {
        pagingFlow.emit(PagingData.from(cities.asList()))
        stateFlow.emit(CitiesViewState(isLoading = false))
    }

    private suspend fun emitEmptyState() {
        pagingFlow.emit(PagingData.from(emptyList()))
        stateFlow.emit(CitiesViewState(isLoading = false))
    }

    private suspend fun emitSelectedCityState(city: City) {
        pagingFlow.emit(PagingData.from(listOf(city)))
        stateFlow.emit(CitiesViewState(selectedCity = city, isLoading = false))
    }

    private suspend fun emitUnselectedCityState(city: City) {
        pagingFlow.emit(PagingData.from(listOf(city)))
        stateFlow.emit(CitiesViewState(selectedCity = null, isLoading = false))
    }

    private fun emitLoadingRefreshState(city: City) = runBlocking {
        pagingFlow.emit(
            PagingData.from(
                listOf(city),
                sourceLoadStates = LoadStates(
                    refresh = LoadStateLoading,
                    prepend = LoadStateNotLoading(true),
                    append = LoadStateNotLoading(true)
                )
            )
        )
        stateFlow.emit(CitiesViewState(isLoading = true))
    }

    private fun emitAppendLoadingState(city: City) = runBlocking {
        pagingFlow.emit(
            PagingData.from(
                listOf(city),
                sourceLoadStates = LoadStates(
                    refresh = LoadStateNotLoading(true),
                    prepend = LoadStateNotLoading(true),
                    append = LoadStateLoading
                )
            )
        )
        stateFlow.emit(CitiesViewState(isLoading = true))
    }

    private fun emitRefreshErrorState(city: City) = runBlocking {
        pagingFlow.emit(
            PagingData.from(
                listOf(city),
                sourceLoadStates = LoadStates(
                    refresh = LoadStateError(Exception("Generic error")),
                    prepend = LoadStateNotLoading(true),
                    append = LoadStateNotLoading(true)
                )
            )
        )
        stateFlow.emit(CitiesViewState(isLoading = true))
    }

    private fun emitAppendErrorState(city: City) = runBlocking {
        pagingFlow.emit(
            PagingData.from(
                listOf(city),
                sourceLoadStates = LoadStates(
                    refresh = LoadStateNotLoading(true),
                    prepend = LoadStateNotLoading(true),
                    append = LoadStateError(Exception("Generic error"))
                )
            )
        )
        stateFlow.emit(CitiesViewState(isLoading = true))
    }

    private fun emitEffect(effect: CitiesEffect) = runBlocking {
        uiEffectFlow.emit(effect)
    }

    @Suppress("UNCHECKED_CAST")
    private fun fixtureViewModel() {
        viewModel = mockk(relaxed = true) {
            every { state } returns stateFlow
            every { uiEffect } returns uiEffectFlow as StateFlow<CitiesEffect>
            every { paginatedCities } returns pagingFlow as StateFlow<PagingData<City>>
        }
    }

    private fun fixtureCoordinator() {
        coodinator = mockk(relaxed = true)
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
