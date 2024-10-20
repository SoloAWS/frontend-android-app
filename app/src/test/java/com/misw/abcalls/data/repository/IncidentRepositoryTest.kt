package com.misw.abcalls.data.repository

import android.content.Context
import com.misw.abcalls.data.api.IncidentApiService
import com.misw.abcalls.data.model.Company
import com.misw.abcalls.data.model.CompanyResponse
import com.misw.abcalls.data.model.Incident
import com.misw.abcalls.data.model.UserIdRequest
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.*

class IncidentRepositoryTest {

    private lateinit var mockApiService: IncidentApiService
    private lateinit var mockContext: Context
    private lateinit var repository: IncidentRepository

    @Before
    fun setup() {
        mockApiService = mock()
        mockContext = mock()
        repository = IncidentRepository(mockApiService, mockContext)
    }

    @Test
    fun `getCompanies returns expected CompanyResponse`() = runBlocking {
        val userId = "testUserId"
        val expectedResponse = CompanyResponse(userId, listOf(Company("1", "Test Company")))

        whenever(mockApiService.getCompanies(UserIdRequest(userId))).thenReturn(expectedResponse)

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
            creation_date = "2024-10-19T14:11:28.989827Z"
        )

        whenever(mockApiService.createIncident(
            description = any(),
            userId = any(),
            companyId = any(),
            file = isNull()
        )).thenReturn(expectedIncident)

        val result = repository.createIncident(description, companyId, userId, null)

        verify(mockApiService).createIncident(
            description = any(),
            userId = any(),
            companyId = any(),
            file = isNull()
        )
        assert(result == expectedIncident)
    }
}