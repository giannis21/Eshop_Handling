package com.example.eshophandling.ui.login.loginResponse.loginresponse

data class CredentialsResponse(
        val `data`: Data,
        val error: List<Any>,
        val success: Int
)