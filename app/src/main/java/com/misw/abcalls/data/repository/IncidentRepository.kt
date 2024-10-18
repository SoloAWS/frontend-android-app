package com.misw.abcalls.data.repository

import android.content.Context
import android.net.Uri
import com.misw.abcalls.data.api.IncidentApiService
import com.misw.abcalls.data.model.Incident
import com.misw.abcalls.data.model.CompanyResponse
import com.misw.abcalls.data.model.UserIdRequest
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class IncidentRepository @Inject constructor(
    private val incidentApiService: IncidentApiService,
    private val context: Context
) {
    suspend fun getCompanies(userId: String): CompanyResponse {
        return incidentApiService.getCompanies(UserIdRequest(userId))
    }

    suspend fun createIncident(description: String, companyId: String, userId: String, fileUri: Uri?): Incident {
        val descriptionPart = description.toRequestBody("text/plain".toMediaTypeOrNull())
        val companyIdPart = companyId.toRequestBody("text/plain".toMediaTypeOrNull())
        val userIdPart = userId.toRequestBody("text/plain".toMediaTypeOrNull())

        val filePart = fileUri?.let { uri ->
            val file = File(context.cacheDir, "temp_file")
            context.contentResolver.openInputStream(uri)?.use { input ->
                file.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
            val requestFile = file.asRequestBody("application/octet-stream".toMediaTypeOrNull())
            MultipartBody.Part.createFormData("file", file.name, requestFile)
        }

        return incidentApiService.createIncident(descriptionPart, userIdPart, companyIdPart, filePart)
    }
}