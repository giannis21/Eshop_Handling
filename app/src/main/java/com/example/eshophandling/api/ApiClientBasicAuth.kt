package com.example.eshophandling.api

import com.example.alertlocation_kotlin.utils.Preferences.token
import com.example.eshophandling.ui.login.loginResponse.loginresponse.CredentialsResponse
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*


interface ApiClientBasicAuth {


    @POST("api/rest_admin/oauth2/token/client_credentials")
    suspend fun getCredentials(@Header("Authorization") h1: String): Response<CredentialsResponse>

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
                    .baseUrl("https://www.status-sparta.gr/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(ApiClientBasicAuth::class.java)
        }
    }

    // https://api.themoviedb.org/3/genre/movie/list?api_key=e7f37ba18b2263f1980dfdd25171d0c2&language=en-US


}