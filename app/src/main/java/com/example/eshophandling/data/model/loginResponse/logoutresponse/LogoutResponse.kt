package com.example.eshophandling.data.model.loginResponse.logoutresponse

data class LogoutResponse(
    val `data`: List<Any>,
    val error: List<String>,
    val success: Int
)