package com.frag.eshophandling.di


import android.content.Context
import com.frag.alertlocation_kotlin.utils.Preferences
import com.frag.eshophandling.MainActivity
import com.frag.eshophandling.data.api.ApiClient
import com.frag.eshophandling.data.api.ApiClientBasicAuth
import com.frag.eshophandling.data.api.BasicAuthInterceptor
import com.frag.eshophandling.data.api.NetworkConnectionIncterceptor
import com.frag.eshophandling.utils.Datastore
import com.frag.eshophandling.utils.DatastoreImpl
import com.google.gson.Gson
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
class NetworkModule {

    @Provides
    @Singleton
    fun provideDataStore(context: Context ) : Datastore = DatastoreImpl(context)

    @Provides
    internal fun provideRetrofitInterface(networkConnectionIncterceptor: NetworkConnectionIncterceptor,secureDatastore: Datastore): ApiClient {

        val interceptor = Interceptor { chain ->
            val url = chain.request().url.newBuilder().build()
            println("basseeeee ${secureDatastore.getBaseUrl()}")
            val request = chain.request()
                .newBuilder()
                .addHeader("Authorization", "Bearer ${secureDatastore.getBaseUrl()}")
                .url(url)
                .build()

            val response = chain.proceed(request)
            synchronized(this) {
                MainActivity.errorListener?.invoke(response.code)
            }

            response
        }


        val logging = HttpLoggingInterceptor()
        logging.apply { logging.level = HttpLoggingInterceptor.Level.BODY }

        val okHttpClient1 = OkHttpClient.Builder()
            .addInterceptor(networkConnectionIncterceptor)
            .addInterceptor(logging)
            .addInterceptor(interceptor)
        return Retrofit.Builder().client(okHttpClient1.build())
            .baseUrl(Preferences.BaseUrl!!)//"https://www.status-sparta.gr/"
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiClient::class.java)
    }


}




