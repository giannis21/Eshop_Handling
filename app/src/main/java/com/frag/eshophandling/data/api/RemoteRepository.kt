package com.frag.eshophandling.data.api



import com.frag.eshophandling.data.model.product_response.Product
import com.frag.eshophandling.data.model.submit_product.SubmittedProduct
import com.frag.eshophandling.data.model.submit_product.SubmittedProductResponse
import com.frag.eshophandling.data.model.submit_products.SubmittedProducts
import com.frag.eshophandling.data.model.loginResponse.loginresponse.CredentialsResponse
import com.frag.eshophandling.data.model.loginResponse.login.LoginUser
import okhttp3.ResponseBody
import retrofit2.Response

class RemoteRepository(private val my_Api: ApiClient, var apiClientBasic: ApiClientBasicAuth) {

    suspend fun login(loginUser: LoginUser, url:String): Response<LoginUser> {
        return my_Api.login(loginUser,url)
    }

    suspend fun getCredentials(authToken: String,url:String): Response<CredentialsResponse> {
        return apiClientBasic.getCredentials(authToken,url)
    }

    suspend fun getProduct(barcode: String): Response<Product> {
        return my_Api.getProduct(barcode)
    }

    suspend fun submitProduct(product: SubmittedProduct): Response<SubmittedProductResponse> {
        return my_Api.submitProduct(product.id.toString(),product)
    }
    suspend fun submitProducts(products: SubmittedProducts): Response<ResponseBody> {
        return my_Api.submitProducts(products)
    }



}