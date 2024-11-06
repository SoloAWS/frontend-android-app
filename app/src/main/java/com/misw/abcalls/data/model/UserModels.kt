package com.misw.abcalls.data.model

data class UserIdRequest(
    val id: String
)

data class UserRegistrationRequest(
    val name: String,
    val email: String,
    val password: String,
    val documentType: String,
    val documentId: String
)

data class UserLoginRequest(
    val username: String,
    val password: String
)

data class AuthResponse(
    val token: String
)

data class UserRegistrationResponse(
    val id: String,
    val email: String,
    val name: String
)