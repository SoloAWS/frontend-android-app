package com.misw.abcalls.data.model

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

data class Incident(
    val id: String,
    val user_id: String,
    val company_id: String,
    val description: String,
    val state: String,
    val channel: String,
    val priority: String,
    val creation_date: String
){
    val formattedDate: String
        get() = try {
            val instant = Instant.parse(creation_date)
            val localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
            val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm", Locale.getDefault())
            localDateTime.format(formatter)
        } catch (e: Exception) {
            creation_date
        }
}

data class IncidentListResponse(
    val incidents: List<Incident>
)