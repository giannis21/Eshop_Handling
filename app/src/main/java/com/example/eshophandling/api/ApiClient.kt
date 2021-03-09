package com.example.eshophandling.api

 import com.example.alertlocation_kotlin.utils.Preferences.token
 import com.example.eshophandling.MainActivity.Companion.errorListener
 import com.example.eshophandling.ui.cards.product_response.Product
 import com.example.eshophandling.ui.cards.submit_product.SubmittedProduct
 import com.example.eshophandling.ui.cards.submit_product.SubmittedProductResponse
 import com.example.eshophandling.ui.cards.submit_products.SubmittedProducts
 import com.example.eshophandling.ui.login.loginResponse.login.LoginUser
 import com.example.eshophandling.ui.login.loginResponse.logoutresponse.LogoutResponse
 import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
 import okhttp3.logging.HttpLoggingInterceptor

 import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*


interface ApiClient {


    @Headers("Content-Type: application/json")
    @POST("api/rest_admin/login")
    suspend fun login(@Body loginUser: LoginUser): Response<LoginUser>

    @Headers("Content-Type: application/json")
    @POST("api/rest_admin/logout")
    suspend fun logout(): Response<LogoutResponse>

    @Headers("Content-Type: application/json")
    @GET("api/rest_admin/products/getproductbysku/{barcode}")
    suspend fun getProduct(@Path("barcode") barcode: String): Response<Product>

    @Headers("Content-Type: application/json")
    @POST("api/rest_admin/products/getproductbysku/")
    suspend fun getProductPerSku(@Body barcode: String): Response<ResponseBody>

    @Headers("Content-Type: application/x-www-form-urlencoded")
    @PUT("api/rest_admin/products/{id}")
    suspend fun submitProduct(@Path("id") id: String, @Body product: SubmittedProduct): Response<SubmittedProductResponse>

    @Headers("Content-Type: application/x-www-form-urlencoded")
    @PUT("api/rest_admin/allproducts/")
    suspend fun submitProducts(@Body products: SubmittedProducts): Response<ResponseBody>

    companion object {

        operator fun invoke(networkConnectionIncterceptor: NetworkConnectionIncterceptor): ApiClient {

            val interceptor = Interceptor { chain ->
                   val url = chain.request().url.newBuilder().build()

                val request = chain.request()
                        .newBuilder()
                        .addHeader("Authorization","Bearer ${token}")
                        .url(url)
                        .build()

                val response = chain.proceed(request)
                synchronized(this) {
                      errorListener?.invoke(response.code)
                }

                response
            }


            println("token = $token")
            val logging = HttpLoggingInterceptor()
            logging.apply { logging.level = HttpLoggingInterceptor.Level.BODY }

            val okHttpClient1 = OkHttpClient.Builder()
                        .addInterceptor(networkConnectionIncterceptor)
                        .addInterceptor(logging)
                        .addInterceptor(interceptor)
            return Retrofit.Builder().client(okHttpClient1.build())
                    .baseUrl("https://www.status-sparta.gr/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(ApiClient::class.java)
        }
    }

    // https://api.themoviedb.org/3/genre/movie/list?api_key=e7f37ba18b2263f1980dfdd25171d0c2&language=en-US


}