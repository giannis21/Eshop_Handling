package com.frag.eshophandling.data.api

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities


object netMethods {

    fun hasInternet(applicationContext: Context) {
            if (!isInternetAvailable(applicationContext)) {
                throw NoInternetException("No internet connection")
             }
    }



    fun isInternetAvailable(applicationContext: Context): Boolean {
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