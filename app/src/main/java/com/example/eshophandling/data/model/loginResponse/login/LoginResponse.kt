package com.example.eshophandling.data.model.loginResponse.login

data class LoginResponse(
        val `data`: Data,
        val error: List<Any>,
        val success: Int
)