package com.example.eshophandling.data.model.loginResponse.loginresponse

data class CredentialsResponse(
        val `data`: Data,
        val error: List<Any>,
        val success: Int
)