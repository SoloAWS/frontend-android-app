package com.misw.abcalls.data.repository

import com.google.gson.JsonParser
import com.misw.abcalls.data.api.TokenManager
import com.misw.abcalls.data.api.UserApiService
import com.misw.abcalls.data.model.*
import retrofit2.HttpException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val userApiService: UserApiService,
    private val tokenManager: TokenManager
) {
    sealed class RegistrationError : Exception() {
        object EmailAlreadyRegistered : RegistrationError()
        object NoCompanyAssociated : RegistrationError()
        class UnknownError(override val message: String) : RegistrationError()
    }

    suspend fun registerUser(
        firstName: String,
        lastName: String,
        email: String,
        password: String,
        documentType: String,
        documentId: String
    ): Result<UserRegistrationResponse> {
        return try {
            val request = UserRegistrationRequest(email, firstName, lastName, documentId, documentType, password)
            val response = userApiService.registerUser(request)
            tokenManager.clearToken()
            tokenManager.saveUserId(response.id)
            Result.success(response)
        } catch (e: HttpException) {
            val errorResult = try {
                val errorBody = e.response()?.errorBody()?.string()
                val errorJson = JsonParser.parseString(errorBody).asJsonObject
                val detail = errorJson.getAsJsonObject("detail")?.get("detail")?.asString

                when (detail) {
                    "Email already registered" -> RegistrationError.EmailAlreadyRegistered
                    "The given user does not belong to any registered company" -> RegistrationError.NoCompanyAssociated
                    else -> RegistrationError.UnknownError(detail ?: "Error en el registro")
                }
            } catch (e: Exception) {
                RegistrationError.UnknownError("Error en el registro")
            }
            Result.failure(errorResult)
        } catch (e: Exception) {
            Result.failure(RegistrationError.UnknownError(e.message ?: "Error en el registro"))
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