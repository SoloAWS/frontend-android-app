package com.misw.abcalls.data.api

import com.misw.abcalls.data.model.Incident
import retrofit2.http.Body
import retrofit2.http.POST

interface IncidentApiService {
    @POST("/incident-management")
    suspend fun createIncident(@Body incident: Incident): Incident
}