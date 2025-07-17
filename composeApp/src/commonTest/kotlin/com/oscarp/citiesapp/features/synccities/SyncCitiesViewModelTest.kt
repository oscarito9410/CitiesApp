package com.oscarp.citiesapp.features.synccities

import com.oscarp.citiesapp.domain.models.CityDownload
import com.oscarp.citiesapp.domain.usecases.HasSyncCitiesUseCase
import com.oscarp.citiesapp.domain.usecases.SyncCitiesUseCase
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.mock
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class SyncCitiesViewModelTest {

    private val dispatcher = UnconfinedTestDispatcher()
    private val syncCitiesUseCase: SyncCitiesUseCase = mock()
    private val hasSyncCitiesUseCase: HasSyncCitiesUseCase = mock()
    private lateinit var viewModel: SyncCitiesViewModel

    @BeforeTest
    fun setupMainDispatcher() {
        Dispatchers.setMain(dispatcher)
        viewModel = SyncCitiesViewModel(
            syncCitiesUseCase,
            hasSyncCitiesUseCase,
            dispatcher
        )
    }

    @AfterTest
    fun tearDownMainDispatcher() {
        Dispatchers.resetMain()
    }

    @Test
    fun `processIntent startSync emits loading  progress updates and completed state`() =
        runTest(dispatcher) {
            // given
            val flow = flowOf(
                CityDownload(
                    totalCities = 200,
                    totalInserted = 50
                ),
                CityDownload(
                    totalCities = 200,
                    totalInserted = 100
                ),
                CityDownload(
                    totalCities = 200,
                    totalInserted = 200
                )
            )
            every { syncCitiesUseCase() } returns flow

            // when
            viewModel.processIntent(SyncIntent.StartSync)
            advanceUntilIdle()

            // then
            val state = viewModel.state.value
            assertEquals(true, state.isCompleted)
            assertEquals(100, state.percentSync)
        }

    @Test
    fun `processIntent StartSync emits error state on failure`() =
        runTest(dispatcher) {
            // given
            val ex = IllegalStateException("network down")
            every { syncCitiesUseCase() } returns flow { throw ex }

            // when
            viewModel.processIntent(SyncIntent.StartSync)
            advanceUntilIdle()

            // then
            val state = viewModel.state.value
            assertTrue(state.isError)
            assertEquals("network down", state.error?.message)
        }

    @Test
    fun `processIntent VerifyLoadSync sets isCompleted true when cities already synced`() = runTest(dispatcher) {
        // given
        everySuspend { hasSyncCitiesUseCase() } returns true

        // when
        viewModel.processIntent(SyncIntent.VerifyLoadSync)
        advanceUntilIdle()

        // then
        val state = viewModel.state.value
        assertTrue(state.isCompleted)
        assertEquals(false, state.isLoading)
    }

    @Test
    fun `processIntent VerifyLoadSync triggers StartSync when no cities are synced`() = runTest(dispatcher) {
        // given
        everySuspend { hasSyncCitiesUseCase() } returns false
        every { syncCitiesUseCase() } returns flowOf(
            CityDownload(
                totalCities = 100,
                totalInserted = 100
            )
        )

        // when
        viewModel.processIntent(SyncIntent.VerifyLoadSync)
        advanceUntilIdle()

        // then
        val state = viewModel.state.value
        assertTrue(state.isCompleted)
        assertEquals(100, state.percentSync)
    }
}
