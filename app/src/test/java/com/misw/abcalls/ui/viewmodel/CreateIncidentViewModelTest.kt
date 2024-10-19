package com.misw.abcalls.ui.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.misw.abcalls.data.model.Company
import com.misw.abcalls.data.model.CompanyResponse
import com.misw.abcalls.data.model.Incident
import com.misw.abcalls.data.repository.IncidentRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.*
import java.time.ZonedDateTime

@ExperimentalCoroutinesApi
class CreateIncidentViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = TestCoroutineDispatcher()
    private val testScope = TestCoroutineScope(testDispatcher)

    private lateinit var mockRepository: IncidentRepository
    private lateinit var viewModel: CreateIncidentViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        mockRepository = mock(IncidentRepository::class.java)
        viewModel = CreateIncidentViewModel(mockRepository)
    }

    @Test
    fun `loadCompanies updates uiState with companies`() = testScope.runBlockingTest {
        val userId = "testUserId"
        val companies = listOf(Company("1", "Test Company"))
        val companyResponse = CompanyResponse(userId, companies)

        `when`(mockRepository.getCompanies(userId)).thenReturn(companyResponse)

        viewModel.loadCompanies(userId)

        assert(viewModel.uiState.value.companies == companies)
        verify(mockRepository).getCompanies(userId)
    }

    @Test
    fun `createIncident updates uiState with created incident`() = testScope.runBlockingTest {
        val description = "Test description"
        val companyId = "testCompanyId"
        val userId = "testUserId"
        val createdIncident = Incident(
            id = "testId",
            user_id = userId,
            company_id = companyId,
            description = description,
            state = "open",
            channel = "mobile",
            priority = "medium",
            creation_date = ZonedDateTime.now()
        )

        `when`(mockRepository.createIncident(description, companyId, userId, null)).thenReturn(createdIncident)

        viewModel.createIncident(description, companyId, userId, null)

        assert(viewModel.uiState.value.createdIncident == createdIncident)
        verify(mockRepository).createIncident(description, companyId, userId, null)
    }

    @Test
    fun `resetState clears createdIncident and error`() = testScope.runBlockingTest {
        viewModel.uiState.value = viewModel.uiState.value.copy(
            createdIncident = mock(),
            error = "Test error"
        )

        viewModel.resetState()

        assert(viewModel.uiState.value.createdIncident == null)
        assert(viewModel.uiState.value.error == null)
    }
}