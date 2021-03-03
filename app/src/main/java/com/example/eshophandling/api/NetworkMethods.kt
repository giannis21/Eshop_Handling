package com.example.eshophandling.api

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build



object netMethods {

    fun hasInternet(applicationContext: Context, return_boolean_state:Boolean) {
        if (Build.VERSION.SDK_INT >= 23) {
            if (!isInternetAvailable1(applicationContext)) {
                 throw NoInternetException("No internet connection")
             }
        } else {
            if (!isInternetAvailable(applicationContext)) {
                throw NoInternetException("No internet connection")
            }
        }

    }

    fun isInternetAvailable(applicationContext: Context): Boolean {

        val connectivityManager = applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
        connectivityManager?.activeNetworkInfo.also {
            return it != null && it.isConnected
        }


    }

    fun isInternetAvailable1(applicationContext: Context): Boolean {
        var result = false
        val connectivityManager = applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?

        connectivityManager?.let {
            val apply = it.getNetworkCapabilities(connectivityManager.activeNetwork)?.apply {
                result = when {
                    hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                    hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                    else -> false
                }
            }
            apply
        }
        return result
    }
}