package com.misw.abcalls.data.repository

import com.misw.abcalls.data.api.TokenManager
import com.misw.abcalls.data.api.UserApiService
import com.misw.abcalls.utils.MainDispatcherRule
import com.misw.abcalls.utils.TestData
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.*
import retrofit2.HttpException
import retrofit2.Response
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue

class UserRepositoryTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var userApiService: UserApiService
    private lateinit var tokenManager: TokenManager
    private lateinit var userRepository: UserRepository

    @Before
    fun setup() {
        userApiService = mock()
        tokenManager = mock()
        userRepository = UserRepository(userApiService, tokenManager)
    }

    @Test
    fun `registerUser success should return success result`() = runTest {
        // Arrange
        val email = "test@test.com"
        val firstName = "Test"
        val lastName = "User"
        val password = "password123"
        val documentType = "passport"
        val documentId = "123456"

        whenever(userApiService.registerUser(any())).thenReturn(TestData.mockUserRegistrationResponse)

        // Act
        val result = userRepository.registerUser(
            firstName,
            lastName,
            email,
            password,
            documentType,
            documentId
        )

        // Assert
        assertTrue(result.isSuccess)
        verify(tokenManager).saveUserId(TestData.mockUserRegistrationResponse.id)
    }

    @Test
    fun `login success should return success result`() = runTest {
        // Arrange
        val email = "test@test.com"
        val password = "password123"
        whenever(userApiService.login(any())).thenReturn(TestData.mockAuthResponse)

        // Act
        val result = userRepository.login(email, password)

        // Assert
        assertTrue(result.isSuccess)
        verify(tokenManager).saveToken(TestData.mockAuthResponse.token)
    }
}
