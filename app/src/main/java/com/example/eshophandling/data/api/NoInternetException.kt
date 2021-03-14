package com.example.eshophandling.data.api

import java.io.IOException

class NoInternetException(message: String) : IOException(message)
class ApiException(message: String) : Exception(message)
class NoInternetExceptio1n : IOException() {
    override val message: String
        get() = "No network available, please check your WiFi or Data connection"
}