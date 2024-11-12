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
import org.junit.Assert.assertNull
import org.junit.Assert.assertNotNull

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

    @Test
    fun `updateFirstName with valid name should not set error`() = runTest {
        // Act
        viewModel.updateFirstName("John")

        // Assert
        assertEquals("John", viewModel.uiState.value.firstName)
        assertNull(viewModel.uiState.value.firstNameError)
    }

    @Test
    fun `updateFirstName with invalid name should set error`() = runTest {
        // Act
        viewModel.updateFirstName("J")

        // Assert
        assertEquals("J", viewModel.uiState.value.firstName)
        assertNotNull(viewModel.uiState.value.firstNameError)
    }

    @Test
    fun `updateLastName with valid name should not set error`() = runTest {
        // Act
        viewModel.updateLastName("Doe")

        // Assert
        assertEquals("Doe", viewModel.uiState.value.lastName)
        assertNull(viewModel.uiState.value.lastNameError)
    }

    @Test
    fun `updatePassword with valid password should not set error`() = runTest {
        // Act
        viewModel.updatePassword("Password123!")

        // Assert
        assertEquals("Password123!", viewModel.uiState.value.password)
        assertNull(viewModel.uiState.value.passwordError)
    }

    @Test
    fun `updatePassword with invalid password should set error`() = runTest {
        // Act
        viewModel.updatePassword("weak")

        // Assert
        assertEquals("weak", viewModel.uiState.value.password)
        assertNotNull(viewModel.uiState.value.passwordError)
    }

    @Test
    fun `updateConfirmPassword with matching password should not set error`() = runTest {
        // Arrange
        viewModel.updatePassword("Password123!")

        // Act
        viewModel.updateConfirmPassword("Password123!")

        // Assert
        assertNull(viewModel.uiState.value.confirmPasswordError)
    }

    @Test
    fun `updateConfirmPassword with non-matching password should set error`() = runTest {
        // Arrange
        viewModel.updatePassword("Password123!")

        // Act
        viewModel.updateConfirmPassword("Password456!")

        // Assert
        assertNotNull(viewModel.uiState.value.confirmPasswordError)
    }

    @Test
    fun `updateTermsAccepted should update state`() = runTest {
        // Act
        viewModel.updateTermsAccepted(true)

        // Assert
        assertTrue(viewModel.uiState.value.termsAccepted)
    }

    @Test
    fun `form should be invalid when required fields are empty`() = runTest {
        // Assert
        assertFalse(viewModel.isFormValid.value)
    }
}