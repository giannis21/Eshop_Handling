package com.example.eshophandling.ui.login.loginResponse.login

data class LoginResponse(
        val `data`: Data,
        val error: List<Any>,
        val success: Int
)