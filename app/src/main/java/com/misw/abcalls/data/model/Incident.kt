package com.misw.abcalls.data.model

data class Incident(
    val id: String? = null,
    val name: String,
    val description: String,
    val attachmentUrl: String? = null
)