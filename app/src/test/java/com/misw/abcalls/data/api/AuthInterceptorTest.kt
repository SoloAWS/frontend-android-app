package com.misw.abcalls.data.api

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.*
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull

class AuthInterceptorTest {
    private lateinit var tokenManager: TokenManager
    private lateinit var interceptor: AuthInterceptor
    private lateinit var chain: Interceptor.Chain

    @Before
    fun setup() {
        tokenManager = mock()
        interceptor = AuthInterceptor(tokenManager)
        chain = mock()
    }

    @Test
    fun `intercept should add authorization header when token exists`() {
        // Arrange
        val token = "test-token"
        val request = Request.Builder()
            .url("https://test.com")
            .build()
        val response = Response.Builder()
            .request(request)
            .protocol(okhttp3.Protocol.HTTP_1_1)
            .code(200)
            .message("OK")
            .build()

        whenever(tokenManager.getToken()).thenReturn(token)
        whenever(chain.request()).thenReturn(request)
        whenever(chain.proceed(any())).thenReturn(response)

        // Act
        interceptor.intercept(chain)

        // Assert
        verify(chain).proceed(check {
            assertEquals("Bearer $token", it.header("Authorization"))
        })
    }

    @Test
    fun `intercept should not add authorization header when token is null`() {
        // Arrange
        val request = Request.Builder()
            .url("https://test.com")
            .build()
        val response = Response.Builder()
            .request(request)
            .protocol(okhttp3.Protocol.HTTP_1_1)
            .code(200)
            .message("OK")
            .build()

        whenever(tokenManager.getToken()).thenReturn(null)
        whenever(chain.request()).thenReturn(request)
        whenever(chain.proceed(any())).thenReturn(response)

        // Act
        interceptor.intercept(chain)

        // Assert
        verify(chain).proceed(check {
            assertNull(it.header("Authorization"))
        })
    }
}