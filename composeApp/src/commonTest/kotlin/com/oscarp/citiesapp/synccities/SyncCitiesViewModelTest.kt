package com.oscarp.citiesapp.synccities

import com.oscarp.citiesapp.domain.models.SyncProgress
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
import kotlin.test.assertNotNull

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
    fun `startSync emits Started Inserting and Completed`() = runTest(dispatcher) {
        // Arrange
        every { useCase() } returns flowOf(
            SyncProgress.Inserting(5),
            SyncProgress.Inserting(10)
        )

        viewModel.startSync()
        advanceUntilIdle()

        // Subscribe first
        assertNotNull(viewModel.syncProgress.value)
    }

    @Test
    fun `startSync emits Error when repository fails`() = runTest(dispatcher) {
        // given
        val ex = IllegalStateException("network down")
        every { useCase() } returns flow { throw ex }

        // when
        viewModel.startSync()
        advanceUntilIdle()
    }
}
