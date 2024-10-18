package com.misw.abcalls.data.model

data class Company(
    val id: String,
    val name: String
)

data class CompanyResponse(
    val userId: String,
    val companies: List<Company>
)