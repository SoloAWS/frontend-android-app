package com.misw.abcalls.utils

import com.misw.abcalls.data.model.*
import java.time.Instant

object TestData {
    val mockIncident = Incident(
        id = "1",
        user_id = "user1",
        company_id = "company1",
        description = "Test incident",
        state = "open",
        channel = "mobile",
        priority = "high",
        creation_date = Instant.now().toString()
    )

    val mockCompany = Company(
        id = "1",
        name = "Test Company"
    )

    val mockCompanies = listOf(
        Company("1", "Test Company 1"),
        Company("2", "Test Company 2"),
        Company("3", "Test Company 3")
    )

    val mockCompanyResponse = CompanyResponse(
        userId = "user1",
        companies = mockCompanies
    )

    val mockUserRegistrationResponse = UserRegistrationResponse(
        id = "1",
        email = "test@test.com",
        name = "Test User"
    )

    val mockAuthResponse = AuthResponse(
        token = "test-token"
    )

    fun createMockIncident(
        id: String = "1",
        description: String = "Test incident",
        state: String = "open",
        priority: String = "high"
    ) = mockIncident.copy(
        id = id,
        description = description,
        state = state,
        priority = priority
    )
}