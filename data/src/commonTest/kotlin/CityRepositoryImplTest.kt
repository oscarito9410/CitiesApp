import app.cash.turbine.test
import com.oscarp.citiesapp.data.importers.CityDataImporter
import com.oscarp.citiesapp.data.remote.CityApiService
import com.oscarp.citiesapp.data.repositories.CityRepositoryImpl
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.core.toByteArray
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class CityRepositoryImplTest {

    @OptIn(ExperimentalCoroutinesApi::class)
    private val dispatcher = UnconfinedTestDispatcher()

    // Create mocks
    private val api: CityApiService = mock()
    private val importer: CityDataImporter = mock()

    private val repo = CityRepositoryImpl(
        api = api,
        importer = importer,
        ioDispatcher = dispatcher
    )

    @Test
    fun `syncCities emits importer values in order`() = runTest(dispatcher) {
        // Arrange: set up behavior with Mokkery
        everySuspend { api.fetchCitiesStream() } returns ByteReadChannel("[]".toByteArray())
        everySuspend { importer.seedFromStream(any(), any()) } returns flowOf(5, 15, 20)

        // Act & Assert: collect and verify emissions
        repo.syncCities().test {
            assertEquals(5, awaitItem())
            assertEquals(15, awaitItem())
            assertEquals(20, awaitItem())
            awaitComplete()
        }
    }
}
