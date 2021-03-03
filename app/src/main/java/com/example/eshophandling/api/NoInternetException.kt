package com.example.eshophandling.api

import java.io.IOException
import java.lang.Exception

class NoInternetException(message: String) : IOException(message)
class ApiException(message: String) : Exception(message)
