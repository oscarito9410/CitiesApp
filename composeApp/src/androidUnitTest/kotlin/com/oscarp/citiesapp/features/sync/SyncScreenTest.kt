@file:OptIn(ExperimentalCoroutinesApi::class)

package com.oscarp.citiesapp.features.sync

import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runComposeUiTest
import com.oscarp.citiesapp.features.synccities.SyncCitiesCoordinator
import com.oscarp.citiesapp.features.synccities.SyncCitiesViewModel
import com.oscarp.citiesapp.features.synccities.SyncViewState
import com.oscarp.citiesapp.robot.SyncScreenRobot
import com.oscarp.citiesapp.testutil.RobolectricComposeTest
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
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
    private lateinit var coordinator: SyncCitiesCoordinator
    private lateinit var stateFlow: MutableStateFlow<SyncViewState>
    private val dispatcher = StandardTestDispatcher()

    @Before
    override fun setup() {
        Dispatchers.setMain(dispatcher)
        stateFlow = MutableStateFlow(SyncViewState(isLoading = true))
        viewModel = mockk(relaxed = true) {
            every { state } returns stateFlow
        }
        coordinator = mockk(relaxed = true)

        super.setup()
    }

    @After
    override fun after() {
        Dispatchers.resetMain()
        super.after()
    }

    @Test
    fun shows_loading_state() = runComposeUiTest {
        val robot = syncScreenRobot()
        with(robot) {
            setScreenContent()
            assertLoadingVisible()
        }
    }

    @Test
    fun shows_no_internet_state_and_triggers_retry() = runComposeUiTest {
        stateFlow.value = SyncViewState(isError = true)

        val robot = syncScreenRobot()
        with(robot) {
            setScreenContent()
            assertNoInternetVisible()
            clickRetryButton()
        }

        verify { coordinator.onRetryClicked() }
    }

    @Test
    fun shows_completed_state_and_navigates() = runComposeUiTest {
        runTest {
            stateFlow.value = SyncViewState(isCompleted = true)

            val robot = syncScreenRobot()
            robot.setScreenContent()

            robot.assertCompletedVisible()
            advanceTimeBy(2.seconds)
            coVerify { coordinator.onSyncCompleted() }
        }
    }

    fun ComposeUiTest.syncScreenRobot(): SyncScreenRobot = SyncScreenRobot(
        composeTestRule = this,
        viewModel = viewModel,
        coordinator = coordinator
    )
}
