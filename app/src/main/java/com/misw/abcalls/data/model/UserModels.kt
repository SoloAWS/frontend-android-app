package com.misw.abcalls.data.model

import com.google.gson.annotations.SerializedName

data class UserIdRequest(
    val id: String
)

data class UserRegistrationRequest(
    @SerializedName("username")
    val email: String,

    @SerializedName("first_name")
    val firstName: String,

    @SerializedName("last_name")
    val lastName: String,

    @SerializedName("document_id")
    val documentId: String,

    @SerializedName("document_type")
    val documentType: String,

    val password: String,

    @SerializedName("birth_date")
    val birthDate: String = "1990-01-01",

    @SerializedName("phone_number")
    val phoneNumber: String = "",

    val importance: Int = 5,

    @SerializedName("allow_call")
    val allowCall: Boolean = true,

    @SerializedName("allow_sms")
    val allowSms: Boolean = true,

    @SerializedName("allow_email")
    val allowEmail: Boolean = true
)

data class UserLoginRequest(
    val username: String,
    val password: String
)

data class AuthResponse(
    @SerializedName("access_token")
    val token: String
)

data class UserRegistrationResponse(
    val id: String,
    val email: String,
    val name: String
)