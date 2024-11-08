package com.misw.abcalls.data.repository

import android.content.Context
import android.net.Uri
import com.misw.abcalls.data.api.IncidentApiService
import com.misw.abcalls.data.api.TokenManager
import com.misw.abcalls.utils.MainDispatcherRule
import com.misw.abcalls.utils.TestData
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.*
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue

class IncidentRepositoryTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var incidentApiService: IncidentApiService
    private lateinit var tokenManager: TokenManager
    private lateinit var context: Context
    private lateinit var incidentRepository: IncidentRepository

    @Before
    fun setup() {
        incidentApiService = mock()
        tokenManager = mock()
        context = mock()
        incidentRepository = IncidentRepository(incidentApiService, tokenManager, context)
    }

    @Test
    fun `getCompanies should return companies when user id exists`() = runTest {
        // Arrange
        whenever(tokenManager.getUserId()).thenReturn("user1")
        whenever(incidentApiService.getCompanies(any())).thenReturn(
            TestData.mockCompany.let { company ->
                com.misw.abcalls.data.model.CompanyResponse(
                    userId = "user1",
                    companies = listOf(company)
                )
            }
        )

        // Act
        val result = incidentRepository.getCompanies()

        // Assert
        verify(incidentApiService).getCompanies(any())
        assertEquals("user1", result?.userId)
        assertEquals(1, result?.companies?.size)
    }

    @Test
    fun `getUserIncidents should return incidents list`() = runTest {
        // Arrange
        whenever(incidentApiService.getUserIncidents()).thenReturn(
            com.misw.abcalls.data.model.IncidentListResponse(
                incidents = listOf(TestData.mockIncident)
            )
        )

        // Act
        val result = incidentRepository.getUserIncidents()

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrNull()?.size)
    }
}