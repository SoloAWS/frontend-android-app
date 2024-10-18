package com.misw.abcalls.data.repository

import com.misw.abcalls.data.api.IncidentApiService
import com.misw.abcalls.data.model.Incident
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class IncidentRepository @Inject constructor(
    private val incidentApiService: IncidentApiService
) {
    suspend fun createIncident(incident: Incident): Result<Incident> {
        return try {
            val response = incidentApiService.createIncident(incident)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}