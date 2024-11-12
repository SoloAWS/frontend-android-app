package com.misw.abcalls.ui.viewmodel

import app.cash.turbine.test
import com.misw.abcalls.data.repository.IncidentRepository
import com.misw.abcalls.utils.MainDispatcherRule
import com.misw.abcalls.utils.TestData
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.*
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue

class IncidentListViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var incidentRepository: IncidentRepository
    private lateinit var viewModel: IncidentListViewModel

    @Before
    fun setup() {
        incidentRepository = mock()
        viewModel = IncidentListViewModel(incidentRepository)
    }

    @Test
    fun `refresh should update incidents list`() = runTest {
        // Arrange
        whenever(incidentRepository.getUserIncidents())
            .thenReturn(Result.success(listOf(TestData.mockIncident)))

        // Act
        viewModel.refresh()

        // Assert
        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(1, state.incidents.size)
            assertEquals(TestData.mockIncident.id, state.incidents[0].id)
            assertNull(state.error)
        }
    }

    @Test
    fun `updateSearchQuery should filter incidents`() = runTest {
        // Arrange
        whenever(incidentRepository.getUserIncidents())
            .thenReturn(Result.success(listOf(TestData.mockIncident)))
        viewModel.refresh()

        // Act
        viewModel.updateSearchQuery("test")

        // Assert
        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(1, state.filteredIncidents.size)
            assertEquals("test", state.searchQuery)
        }
    }

    @Test
    fun `updateSearchQuery with empty query should show all incidents`() = runTest {
        // Arrange
        whenever(incidentRepository.getUserIncidents())
            .thenReturn(Result.success(listOf(TestData.mockIncident)))
        viewModel.refresh()

        // Act
        viewModel.updateSearchQuery("")

        // Assert
        assertEquals(1, viewModel.uiState.value.filteredIncidents.size)
    }

    @Test
    fun `refresh should handle network error`() = runTest {
        // Arrange
        whenever(incidentRepository.getUserIncidents())
            .thenReturn(Result.failure(Exception("Network error")))

        // Act
        viewModel.refresh()

        // Assert
        assertNotNull(viewModel.uiState.value.error)
        assertTrue(viewModel.uiState.value.incidents.isEmpty())
    }

    @Test
    fun `retryLoading should clear error state and reload incidents`() = runTest {
        // Arrange
        whenever(incidentRepository.getUserIncidents())
            .thenReturn(Result.success(listOf(TestData.mockIncident)))

        // Act
        viewModel.retryLoading()

        // Assert
        assertNull(viewModel.uiState.value.error)
        assertEquals(1, viewModel.uiState.value.incidents.size)
    }
}