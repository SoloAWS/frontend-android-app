package com.misw.abcalls.data.api

import com.misw.abcalls.data.model.Incident
import com.misw.abcalls.data.model.CompanyResponse
import com.misw.abcalls.data.model.UserIdRequest
import retrofit2.http.POST
import retrofit2.http.Body
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Multipart
import retrofit2.http.Part

interface IncidentApiService {
    @Multipart
    @POST("/incident-management/user-incident")
    suspend fun createIncident(
        @Part("description") description: RequestBody,
        @Part("user_id") userId: RequestBody,
        @Part("company_id") companyId: RequestBody,
        @Part file: MultipartBody.Part?
    ): Incident

    @POST("/user-management/user/companies-user")
    suspend fun getCompanies(@Body userIdRequest: UserIdRequest): CompanyResponse
}