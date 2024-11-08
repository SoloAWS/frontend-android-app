package com.misw.abcalls.ui.viewmodel

import android.net.Uri
import com.misw.abcalls.data.model.Company
import com.misw.abcalls.data.model.CompanyResponse
import com.misw.abcalls.data.repository.IncidentRepository
import com.misw.abcalls.utils.MainDispatcherRule
import com.misw.abcalls.utils.TestData
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.*

class CreateIncidentViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var incidentRepository: IncidentRepository
    private lateinit var viewModel: CreateIncidentViewModel
    private lateinit var mockUri: Uri

    @Before
    fun setup() {
        incidentRepository = mock()
        viewModel = CreateIncidentViewModel(incidentRepository)
        mockUri = mock()
    }

    @Test
    fun `loadCompanies success should update state with companies`() = runTest {
        // Arrange
        val companies = listOf(
            Company("1", "Company 1"),
            Company("2", "Company 2")
        )
        whenever(incidentRepository.getCompanies()).thenReturn(
            CompanyResponse("user1", companies)
        )

        // Initial state check
        assertTrue(viewModel.uiState.value.companies.isEmpty())
        assertNull(viewModel.uiState.value.error)

        // Act
        viewModel.loadCompanies()

        // Assert
        with(viewModel.uiState.value) {
            assertEquals(companies, companies)
            assertTrue(!isLoading)
            assertNull(error)
        }
    }

    @Test
    fun `loadCompanies failure should update state with error`() = runTest {
        // Arrange
        val errorMessage = "Failed to load companies"
        whenever(incidentRepository.getCompanies()).thenThrow(RuntimeException(errorMessage))

        // Act
        viewModel.loadCompanies()

        // Assert
        with(viewModel.uiState.value) {
            assertEquals(errorMessage, error)
            assertTrue(!isLoading)
            assertTrue(companies.isEmpty())
        }
    }

    @Test
    fun `createIncident success should update state with created incident`() = runTest {
        // Arrange
        val description = "Test incident"
        val companyId = "1"
        whenever(incidentRepository.createIncident(description, companyId, mockUri))
            .thenReturn(TestData.mockIncident)

        // Act
        viewModel.createIncident(description, companyId, mockUri)

        // Assert
        with(viewModel.uiState.value) {
            assertEquals(TestData.mockIncident, createdIncident)
            assertTrue(!isLoading)
            assertNull(error)
        }
    }

    @Test
    fun `createIncident failure should update state with error`() = runTest {
        // Arrange
        val description = "Test incident"
        val companyId = "1"
        val errorMessage = "Failed to create incident"
        whenever(incidentRepository.createIncident(description, companyId, mockUri))
            .thenThrow(RuntimeException(errorMessage))

        // Act
        viewModel.createIncident(description, companyId, mockUri)

        // Assert
        with(viewModel.uiState.value) {
            assertEquals(errorMessage, error)
            assertTrue(!isLoading)
            assertNull(createdIncident)
        }
    }

    @Test
    fun `createIncident with null file should work`() = runTest {
        // Arrange
        val description = "Test incident"
        val companyId = "1"
        whenever(incidentRepository.createIncident(description, companyId, null))
            .thenReturn(TestData.mockIncident)

        // Act
        viewModel.createIncident(description, companyId, null)

        // Assert
        with(viewModel.uiState.value) {
            assertEquals(TestData.mockIncident, createdIncident)
            assertTrue(!isLoading)
            assertNull(error)
        }
    }

    @Test
    fun `resetState should clear incident and error`() = runTest {
        // Arrange
        val description = "Test incident"
        val companyId = "1"
        whenever(incidentRepository.createIncident(description, companyId, null))
            .thenReturn(TestData.mockIncident)

        viewModel.createIncident(description, companyId, null)

        // Verify incident was created
        assertEquals(TestData.mockIncident, viewModel.uiState.value.createdIncident)

        // Act
        viewModel.resetState()

        // Assert
        with(viewModel.uiState.value) {
            assertNull(createdIncident)
            assertNull(error)
        }
    }

    @Test
    fun `consecutive createIncident calls should work independently`() = runTest {
        // Arrange
        val description1 = "Test incident 1"
        val description2 = "Test incident 2"
        val companyId = "1"

        val incident1 = TestData.mockIncident.copy(id = "1", description = description1)
        val incident2 = TestData.mockIncident.copy(id = "2", description = description2)

        whenever(incidentRepository.createIncident(description1, companyId, null))
            .thenReturn(incident1)
        whenever(incidentRepository.createIncident(description2, companyId, null))
            .thenReturn(incident2)

        // First incident
        viewModel.createIncident(description1, companyId, null)
        assertEquals(incident1, viewModel.uiState.value.createdIncident)

        // Reset
        viewModel.resetState()
        assertNull(viewModel.uiState.value.createdIncident)

        // Second incident
        viewModel.createIncident(description2, companyId, null)
        assertEquals(incident2, viewModel.uiState.value.createdIncident)
    }
}