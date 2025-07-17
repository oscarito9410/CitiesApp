package com.oscarp.citiesapp.domain.usecases

import com.oscarp.citiesapp.domain.repositories.CityRepository
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.mock
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class HasSyncCitiesUseCaseTest {

    private val repository: CityRepository = mock()
    private val useCase = HasSyncCitiesUseCase(repository)

    @Test
    fun `returns true when repository has synced cities`() = runTest {
        // given
        everySuspend { repository.hasSyncCities() } returns true

        // when
        val result = useCase()

        // then
        assertTrue(result)
    }

    @Test
    fun `returns false when repository has no synced cities`() = runTest {
        // given
        everySuspend { repository.hasSyncCities() } returns false

        // when
        val result = useCase()

        // then
        assertFalse(result)
    }
}
