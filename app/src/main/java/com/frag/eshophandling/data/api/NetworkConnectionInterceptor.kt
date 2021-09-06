package com.frag.eshophandling.data.api

import android.content.Context
import okhttp3.Interceptor

import okhttp3.Response
import javax.inject.Inject


class NetworkConnectionIncterceptor @Inject constructor(context: Context) : Interceptor {

    private val applicationContext: Context = context.applicationContext

    override fun intercept(chain: Interceptor.Chain): Response {
        netMethods.hasInternet(applicationContext)
        return chain.proceed(chain.request())
    }
}