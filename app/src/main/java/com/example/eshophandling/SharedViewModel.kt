package com.example.eshophandling

import android.content.Context
import android.util.Base64
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.alertlocation_kotlin.utils.Preferences
import com.example.eshophandling.api.NoInternetException
import com.example.eshophandling.api.RemoteRepository
import com.example.eshophandling.ui.Loading_dialog
import com.example.eshophandling.ui.cards.product_response.Data
import com.example.eshophandling.ui.cards.submit_product.SubmittedProduct
import com.example.eshophandling.ui.login.loginResponse.login.LoginUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.UnsupportedEncodingException

class SharedViewModel(var remoteRepository: RemoteRepository, var context: Context) : ViewModel() {

    var allProducts: MutableLiveData<MutableList<Data>>
    var loading = MutableLiveData<Boolean>(false)
    var noInternetException = MutableLiveData<Boolean>(false)
    var error = MutableLiveData<Boolean>(false)
    var updated = MutableLiveData<Boolean>(false)
    var productRetrieved = MutableLiveData<Boolean>(false)
    var productExists = MutableLiveData<Boolean>(false)
    var productNotFound=MutableLiveData(false)
    val dialog: Loading_dialog

    init {
        allProducts = MutableLiveData<MutableList<Data>>()
        allProducts.value= mutableListOf()
        dialog = Loading_dialog(context)
    }

    fun getProduct(barcode: String?) {
        loading.postValue(true)

        dialog.displayLoadingDialog()
        viewModelScope.launch(Dispatchers.Default) {
            runCatching {
                remoteRepository.getProduct(barcode ?: "0002")
            }.onFailure {
                error.value= true
                loading.postValue(false)
                dialog.hideLoadingDialog()

                if(it is NoInternetException){
                    noInternetException.postValue(true)
                }

            }.onSuccess {
               dialog.hideLoadingDialog()
               if(it.isSuccessful){
                   if(it.body()!!.success == 1 )
                      addProductToList(it.body()!!.data)
               }else{
                    loading.postValue(false)

                   if(it.code() == 404){
                       productNotFound.postValue(true)
                   }else if(it.code() == 401){

                   }else{
                       error.postValue(true)
                   }
                }
            }
        }
    }

    private fun addProductToList(product: Data) {

        val product1= allProducts.value?.find { it.id == product.id  }
        if(product1 == null)
        {
            productRetrieved.postValue(true)
            val tempList=allProducts.value
            tempList?.add(product)
            allProducts.postValue(tempList)
        }else{
            productExists.postValue(true)
        }

    }

    fun submitProduct(product: SubmittedProduct) {
        dialog.displayLoadingDialog()

        viewModelScope.launch(Dispatchers.Default) {
            runCatching {
                remoteRepository.submitProduct(product)
            }.onFailure {

                error.value= true
                loading.postValue(false)
                dialog.hideLoadingDialog()

                if(it is NoInternetException){
                    noInternetException.postValue(true)
                }

            }.onSuccess {
                loading.postValue(false)
                dialog.hideLoadingDialog()

                if(it.isSuccessful){
                    if(it.body()!!.success == 1 )
                        updated.postValue(true)
                }else{
                    error.postValue(true)
                }
            }
        }
    }

    fun deleteProduct(id: Int) {
        allProducts.value?.find { it.id == id }?.let {
            allProducts.value?.remove(it)
        }
        allProducts.notifyObserver()
    }

    private fun <T> MutableLiveData<T>.notifyObserver() {
        this.value = this.value
    }

    fun removeItemAt(position: Int) {
           var product = allProducts.value?.get(position)
           product?.let {
               allProducts.value?.remove(it)
               allProducts.notifyObserver()
           }

    }

  }



