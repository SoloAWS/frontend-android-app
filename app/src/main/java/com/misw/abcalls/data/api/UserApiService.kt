package com.misw.abcalls.data.api

import com.misw.abcalls.data.model.*
import retrofit2.http.Body
import retrofit2.http.POST

interface UserApiService {
    @POST("/user/user/")
    suspend fun registerUser(@Body request: UserRegistrationRequest): UserRegistrationResponse

    @POST("/auth/login")
    suspend fun login(@Body request: UserLoginRequest): AuthResponse
}