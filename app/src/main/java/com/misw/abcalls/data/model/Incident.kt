package com.misw.abcalls.data.model

data class Incident(
    val id: String,
    val user_id: String,
    val company_id: String,
    val description: String,
    val state: String,
    val channel: String,
    val priority: String,
    val creation_date: String
)