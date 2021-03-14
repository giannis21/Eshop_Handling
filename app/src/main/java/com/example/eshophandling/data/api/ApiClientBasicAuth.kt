package com.example.eshophandling.data.api

import android.content.Context
import com.example.alertlocation_kotlin.utils.Preferences
import com.example.alertlocation_kotlin.utils.Preferences.BaseUrl
import com.example.alertlocation_kotlin.utils.Preferences.token
import com.example.eshophandling.data.model.loginResponse.loginresponse.CredentialsResponse
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Url


interface ApiClientBasicAuth {


    @POST()
    suspend fun getCredentials(@Header("Authorization") h1: String, @Url url: String): Response<CredentialsResponse>

    companion object {

        operator fun invoke(networkConnectionIncterceptor: NetworkConnectionIncterceptor): ApiClientBasicAuth {


            println("token = $token")
           val logging = HttpLoggingInterceptor()
           logging.apply { logging.level = HttpLoggingInterceptor.Level.BODY }


            val okHttpClient1 = OkHttpClient.Builder()
                     .addInterceptor(BasicAuthInterceptor("demo", "demo"))
                     .addInterceptor(networkConnectionIncterceptor)
                     .addInterceptor(logging)

            return Retrofit.Builder().client(okHttpClient1.build())
                    .baseUrl(BaseUrl!!)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(ApiClientBasicAuth::class.java)
        }


    }

    // https://api.themoviedb.org/3/genre/movie/list?api_key=e7f37ba18b2263f1980dfdd25171d0c2&language=en-US


}