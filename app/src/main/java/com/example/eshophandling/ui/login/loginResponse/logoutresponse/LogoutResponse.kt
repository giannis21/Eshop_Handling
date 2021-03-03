package com.example.eshophandling.ui.login.loginResponse.logoutresponse

data class LogoutResponse(
    val `data`: List<Any>,
    val error: List<String>,
    val success: Int
)