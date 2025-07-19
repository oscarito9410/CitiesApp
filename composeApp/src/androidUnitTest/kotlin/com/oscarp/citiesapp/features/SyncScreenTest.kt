@file:OptIn(ExperimentalCoroutinesApi::class)

package com.oscarp.citiesapp.features

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import androidx.navigation.NavController
import androidx.navigation.NavOptionsBuilder
import com.oscarp.citiesapp.features.synccities.SyncCitiesViewModel
import com.oscarp.citiesapp.features.synccities.SyncIntent
import com.oscarp.citiesapp.features.synccities.SyncScreen
import com.oscarp.citiesapp.features.synccities.SyncViewState
import com.oscarp.citiesapp.features.synccities.TagCompleted
import com.oscarp.citiesapp.features.synccities.TagLoading
import com.oscarp.citiesapp.features.synccities.TagNotInternet
import com.oscarp.citiesapp.features.synccities.TagRetryButton
import com.oscarp.citiesapp.navigation.CitiesDestination
import com.oscarp.citiesapp.testutil.RobolectricComposeTest
import com.oscarp.citiesapp.ui.theme.AppTheme
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalTestApi::class)
class SyncScreenTest : RobolectricComposeTest() {

    private lateinit var viewModel: SyncCitiesViewModel
    private lateinit var stateFlow: MutableStateFlow<SyncViewState>
    private val navigationController = mockk<NavController>(relaxed = true)
    private val testDispatcher = StandardTestDispatcher()
    private val testScope = TestScope(testDispatcher)

    @Before
    override fun setup() {
        Dispatchers.setMain(testDispatcher)
        stateFlow = MutableStateFlow(SyncViewState(isLoading = true, percentSync = 0))
        viewModel = mockk(relaxed = true) {
            every { state } returns stateFlow
        }
        super.setup()
    }

    @After
    override fun after() {
        Dispatchers.resetMain()
        super.after()
    }

    @Test
    fun shows_loading_state() = runComposeUiTest(effectContext = testDispatcher) {
        setContent {
            AppTheme {
                SyncScreen(
                    navController = navigationController,
                    viewModel = viewModel
                )
            }
        }
        onNodeWithTag(TagLoading).assertIsDisplayed()
    }

    @Test
    fun shows_no_internet_state_and_click_retry() = runComposeUiTest {
        testScope.runTest {
            stateFlow.value = SyncViewState(isError = true)
            every { viewModel.processIntent(SyncIntent.StartSync) } just Runs

            setContent {
                AppTheme {
                    SyncScreen(
                        navController = navigationController,
                        viewModel = viewModel
                    )
                }
            }

            onNodeWithTag(TagNotInternet).assertIsDisplayed()
            onNodeWithTag(TagRetryButton).performClick()

            verify {
                viewModel.processIntent(SyncIntent.StartSync)
            }
        }
    }

    @Test
    fun shows_completed_state() = runComposeUiTest {
        testScope.runTest {
            stateFlow.value = SyncViewState(isCompleted = true)
            setContent {
                AppTheme {
                    SyncScreen(
                        navController = navigationController,
                        viewModel = viewModel
                    )
                }
            }

            onNodeWithTag(TagCompleted).assertIsDisplayed()
            advanceTimeBy(2.seconds)
            verify {
                navigationController.navigate(
                    CitiesDestination,
                    any<NavOptionsBuilder.() -> Unit>()
                )
            }
        }
    }
}
