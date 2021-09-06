package com.frag.eshophandling.di


import com.frag.alertlocation_kotlin.utils.Preferences
import com.frag.eshophandling.MainActivity
import com.frag.eshophandling.data.api.ApiClient
import com.frag.eshophandling.data.api.ApiClientBasicAuth
import com.frag.eshophandling.data.api.BasicAuthInterceptor
import com.frag.eshophandling.data.api.NetworkConnectionIncterceptor
import dagger.Module
import dagger.Provides
import dagger.Reusable
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit

import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
class NetworkModuleBasicAuth {

  @Singleton
  @Provides
  internal fun provideBasicAuthInstance(networkConnectionIncterceptor: NetworkConnectionIncterceptor): ApiClientBasicAuth {

    val logging = HttpLoggingInterceptor()
    logging.apply { logging.level = HttpLoggingInterceptor.Level.BODY }


    val okHttpClient1 = OkHttpClient.Builder()
        .addInterceptor(BasicAuthInterceptor("demo", "demo"))
        .addInterceptor(networkConnectionIncterceptor)
        .addInterceptor(logging)

    return Retrofit.Builder().client(okHttpClient1.build())
        .baseUrl(Preferences.BaseUrl!!)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(ApiClientBasicAuth::class.java)
   }
}




