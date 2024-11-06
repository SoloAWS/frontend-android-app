package com.misw.abcalls.data.repository

import com.misw.abcalls.data.api.TokenManager
import com.misw.abcalls.data.api.UserApiService
import com.misw.abcalls.data.model.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val userApiService: UserApiService,
    private val tokenManager: TokenManager
) {
    suspend fun registerUser(
        name: String,
        email: String,
        password: String,
        documentType: String,
        documentId: String
    ): Result<UserRegistrationResponse> {
        return try {
            val request = UserRegistrationRequest(name, email, password, documentType, documentId)
            val response = userApiService.registerUser(request)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun login(email: String, password: String): Result<AuthResponse> {
        return try {
            val request = UserLoginRequest(email, password)
            val response = userApiService.login(request)
            tokenManager.saveToken(response.token)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}