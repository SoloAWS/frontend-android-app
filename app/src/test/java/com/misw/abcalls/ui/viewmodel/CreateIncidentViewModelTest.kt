package com.misw.abcalls.ui.viewmodel

import android.net.Uri
import app.cash.turbine.test
import com.misw.abcalls.data.api.IncidentApiService
import com.misw.abcalls.data.model.Company
import com.misw.abcalls.data.model.CompanyResponse
import com.misw.abcalls.data.model.Incident
import com.misw.abcalls.data.repository.IncidentRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.advanceUntilIdle
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.*
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalCoroutinesApi::class)
class CreateIncidentViewModelTest {

    private val dispatcher = StandardTestDispatcher()
    private lateinit var incidentRepository: IncidentRepository
    private lateinit var incidentApiService: IncidentApiService
    private lateinit var viewModel: CreateIncidentViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(StandardTestDispatcher())

        // Mock the IncidentApiService
        incidentApiService = mock()

        // Create a real IncidentRepository with the mocked IncidentApiService
        incidentRepository = IncidentRepository(incidentApiService, mock())

        viewModel = CreateIncidentViewModel(incidentRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain() // Reset the Main dispatcher after tests
    }

    @Test
    fun `loadCompanies successfully loads companies`() = runTest(dispatcher) {
        // Prepare mock data
        val mockCompanies = listOf(Company("1", "Company A"), Company("2", "Company B"))
        val mockResponse = CompanyResponse("user123", mockCompanies)
        whenever(incidentRepository.getCompanies(eq("user123"))).thenReturn(mockResponse)

        // Run the test
        viewModel.loadCompanies("user123")

        advanceUntilIdle()
        println("Current UI State: ${viewModel.uiState.value}")
        // Assert the final state
        assertEquals(mockCompanies, viewModel.uiState.value.companies)
        assertEquals(false, viewModel.uiState.value.isLoading)

        verify(incidentApiService).getCompanies(any())
    }

    @OptIn(ExperimentalTime::class)
    @Test
    fun `loadCompanies sets error when repository throws exception`() = runTest(dispatcher) {
        // Setup exception
        whenever(incidentRepository.getCompanies(any())).thenThrow(RuntimeException("Network error"))

        // Run the test
        viewModel.loadCompanies("user123")

        // Collect flow emissions
        viewModel.uiState.test {
            assertEquals(awaitItem().isLoading, true)
            assertEquals(awaitItem().error, "Network error")
            assertEquals(awaitItem().isLoading, false)
        }
    }

    @OptIn(ExperimentalTime::class)
    @Test
    fun `createIncident successfully creates an incident`() = runTest(dispatcher) {
        // Prepare mock data
        val mockIncident = Incident(
            id = "testId",
            user_id = "userId",
            company_id = "companyId",
            description = "description",
            state = "open",
            channel = "mobile",
            priority = "medium",
            creation_date = "2024-10-19T14:11:28.989827Z"
        )
        whenever(incidentRepository.createIncident(any(), any(), any(), any())).thenReturn(mockIncident)

        // Run the test
        viewModel.createIncident("description", "companyId", "userId", null)

        // Collect flow emissions
        viewModel.uiState.test {
            assertEquals(awaitItem().isLoading, true)
            assertEquals(awaitItem().createdIncident, mockIncident)
            assertEquals(awaitItem().isLoading, false)
        }
    }

    @OptIn(ExperimentalTime::class)
    @Test
    fun `createIncident sets error when repository throws exception`() = runTest(dispatcher) {
        // Setup exception
        whenever(incidentRepository.createIncident(any(), any(), any(), any())).thenThrow(RuntimeException("Error creating incident"))

        // Run the test
        viewModel.createIncident("description", "companyId", "userId", null)

        // Collect flow emissions
        viewModel.uiState.test {
            assertEquals(awaitItem().isLoading, true)
            assertEquals(awaitItem().error, "Error creating incident")
            assertEquals(awaitItem().isLoading, false)
        }
    }

    @Test
    fun `resetState resets uiState to default values`() = runTest(dispatcher) {
        // Assume initial state with some data
        viewModel.createIncident("description", "companyId", "userId", null)
        advanceUntilIdle()  // Let the coroutine finish

        // Reset state
        viewModel.resetState()

        // Collect the UI state and assert default values
        val uiState = viewModel.uiState.first()
        assertEquals(uiState.createdIncident, null)
        assertEquals(uiState.error, null)
    }
}
