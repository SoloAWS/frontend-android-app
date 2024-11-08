package com.misw.abcalls.ui.viewmodel

import app.cash.turbine.test
import com.misw.abcalls.data.repository.UserRepository
import com.misw.abcalls.utils.MainDispatcherRule
import com.misw.abcalls.utils.TestData
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.*
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.assertFalse

class UserRegistrationViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var userRepository: UserRepository
    private lateinit var viewModel: UserRegistrationViewModel

    @Before
    fun setup() {
        userRepository = mock()
        viewModel = UserRegistrationViewModel(userRepository)
    }

    @Test
    fun `updateEmail with valid email should not set error`() = runTest {
        // Act
        viewModel.updateEmail("test@example.com")

        // Assert
        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals("test@example.com", state.email)
            assertEquals(null, state.emailError)
        }
    }

    @Test
    fun `updateEmail with invalid email should set error`() = runTest {
        // Act
        viewModel.updateEmail("invalid-email")

        // Assert
        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals("invalid-email", state.email)
            assertTrue(state.emailError != null)
        }
    }

    @Test
    fun `register with valid data should trigger registration`() = runTest {
        // Arrange
        whenever(userRepository.registerUser(any(), any(), any(), any(), any(), any()))
            .thenReturn(Result.success(TestData.mockUserRegistrationResponse))
        whenever(userRepository.login(any(), any()))
            .thenReturn(Result.success(TestData.mockAuthResponse))

        // Setup valid form data
        viewModel.updateFirstName("John")
        viewModel.updateLastName("Doe")
        viewModel.updateEmail("john@example.com")
        viewModel.updatePassword("Password123!")
        viewModel.updateConfirmPassword("Password123!")
        viewModel.updateTermsAccepted(true)

        // Act
        viewModel.register()

        // Assert
        verify(userRepository).registerUser(any(), any(), any(), any(), any(), any())
        verify(userRepository).login(any(), any())

        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state.registrationSuccess)
            assertFalse(state.isLoading)
        }
    }
}