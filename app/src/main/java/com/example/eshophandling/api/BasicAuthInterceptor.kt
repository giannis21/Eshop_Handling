package com.example.eshophandling.api

import android.util.Base64
import okhttp3.Credentials
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import java.io.UnsupportedEncodingException

class BasicAuthInterceptor(user: String?, password: String?) : Interceptor {
    private val credentials: String

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request: Request = chain.request()
        val authenticatedRequest: Request = request.newBuilder().header("Authorization", credentials).build()
        return chain.proceed(authenticatedRequest)
    }

    init {
        credentials = Credentials.basic(user!!, password!!)
    }

    fun getAuthToken( username:String ="demo",  password: String ="demo"): String {
        var data = ByteArray(0)
        try {
            data = ("$username:$password").toByteArray(charset("UTF-8"))
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }
        return "Basic " + Base64.encodeToString(data, Base64.NO_WRAP)
    }
}