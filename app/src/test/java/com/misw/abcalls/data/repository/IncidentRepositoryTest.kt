package com.misw.abcalls.data.repository

import com.misw.abcalls.data.api.IncidentApiService
import com.misw.abcalls.data.model.Company
import com.misw.abcalls.data.model.CompanyResponse
import com.misw.abcalls.data.model.Incident
import com.misw.abcalls.data.model.UserIdRequest
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*
import java.time.ZonedDateTime

class IncidentRepositoryTest {

    private lateinit var mockApiService: IncidentApiService
    private lateinit var repository: IncidentRepository

    @Before
    fun setup() {
        mockApiService = mock(IncidentApiService::class.java)
        repository = IncidentRepository(mockApiService, mock())
    }

    @Test
    fun `getCompanies returns expected CompanyResponse`() = runBlocking {
        val userId = "testUserId"
        val expectedResponse = CompanyResponse(userId, listOf(Company("1", "Test Company")))

        `when`(mockApiService.getCompanies(UserIdRequest(userId))).thenReturn(expectedResponse)

        val result = repository.getCompanies(userId)

        verify(mockApiService).getCompanies(UserIdRequest(userId))
        assert(result == expectedResponse)
    }

    @Test
    fun `createIncident returns expected Incident`() = runBlocking {
        val description = "Test description"
        val companyId = "testCompanyId"
        val userId = "testUserId"
        val expectedIncident = Incident(
            id = "testId",
            user_id = userId,
            company_id = companyId,
            description = description,
            state = "open",
            channel = "mobile",
            priority = "medium",
            creation_date = ZonedDateTime.now()
        )

        `when`(mockApiService.createIncident(any(), any(), any(), any())).thenReturn(expectedIncident)

        val result = repository.createIncident(description, companyId, userId, null)

        verify(mockApiService).createIncident(any(), any(), any(), any())
        assert(result == expectedIncident)
    }
}