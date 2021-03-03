package com.example.eshophandling.api



import com.example.eshophandling.ui.cards.product_response.Product
import com.example.eshophandling.ui.cards.submit_product.SubmittedProduct
import com.example.eshophandling.ui.cards.submit_product.SubmittedProductResponse
import com.example.eshophandling.ui.login.loginResponse.loginresponse.CredentialsResponse
import com.example.eshophandling.ui.login.loginResponse.login.LoginUser
import retrofit2.Response

class RemoteRepository(private val my_Api: ApiClient,var apiClientBasic: ApiClientBasicAuth) {

    suspend fun login(loginUser: LoginUser): Response<LoginUser> {
        return my_Api.login(loginUser)
    }

    suspend fun getCredentials(authToken: String): Response<CredentialsResponse> {
        return apiClientBasic.getCredentials(authToken)
    }

    suspend fun getProduct(barcode: String): Response<Product> {
        return my_Api.getProduct(barcode)
    }

    suspend fun submitProduct(product: SubmittedProduct): Response<SubmittedProductResponse> {
        return my_Api.submitProduct(product.id.toString(),product)
    }


}