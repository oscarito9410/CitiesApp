package com.oscarp.citiesapp.synccities

import com.oscarp.citiesapp.domain.usecases.SyncCitiesUseCase
import dev.mokkery.answering.returns
import dev.mokkery.every
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
    private val useCase: SyncCitiesUseCase = mock()
    private val viewModel = SyncCitiesViewModel(
        useCase,
        dispatcher
    )

    @BeforeTest
    fun setupMainDispatcher() {
        Dispatchers.setMain(dispatcher)
    }

    @AfterTest
    fun tearDownMainDispatcher() {
        Dispatchers.resetMain()
    }

    @Test
    fun `processIntent StartSync emits Loading Inserting and Completed in order`() =
        runTest(dispatcher) {
            // stub the use case to emit 5 then 10
            every { useCase() } returns flowOf(5, 10)

            assertEquals(SyncViewState.Idle, viewModel.state.value)

            viewModel.processIntent(SyncIntent.StartSync)
            advanceUntilIdle()

            val state = viewModel.state.value
            assertEquals(SyncViewState.Completed, state)
        }

    @Test
    fun `processIntent StartSync on error emits Idle Loading Completed and Error state`() =
        runTest(dispatcher) {
            // given
            val ex = IllegalStateException("network down")
            every { useCase() } returns flow { throw ex }

            // when
            viewModel.processIntent(SyncIntent.StartSync)
            advanceUntilIdle()

            // then
            val errorState = viewModel.state.value
            assertTrue(errorState is SyncViewState.Error)
            assertEquals("network down", errorState.error.message)
        }
}
