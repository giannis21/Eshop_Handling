package com.example.eshophandling

import android.content.Context
import androidx.core.os.trace
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.alertlocation_kotlin.utils.Preferences.username
import com.example.eshophandling.api.NoInternetException
import com.example.eshophandling.api.RemoteRepository
import com.example.eshophandling.ui.Loading_dialog
import com.example.eshophandling.ui.cards.product_response.Data
import com.example.eshophandling.ui.cards.submit_product.SubmittedProduct
import com.example.eshophandling.ui.cards.submit_products.Data1
import com.example.eshophandling.ui.cards.submit_products.SubmittedProducts
import com.example.eshophandling.utils.milliToDate
import kotlinx.coroutines.*
import java.io.IOException
import java.util.*

class SharedViewModel(var remoteRepository: RemoteRepository, var context: Context) : ViewModel() {

    var allProducts: MutableLiveData<MutableList<Data>>
    var allProducts_initial: MutableLiveData<MutableList<Data>>
    var loading = MutableLiveData<Boolean>(false)
    var noInternetException = MutableLiveData<Boolean>(false)
    var error = MutableLiveData<Boolean>(false)
    var updated = MutableLiveData<Boolean>(false)
    var productRetrieved = MutableLiveData<Boolean>(false)
    var productExists = MutableLiveData<Boolean>(false)
    var productNotFound=MutableLiveData(false)
    val dialog: Loading_dialog
    var job:Job= Job()

    init {
        allProducts = MutableLiveData<MutableList<Data>>()
        allProducts.value= mutableListOf()

        allProducts_initial = MutableLiveData<MutableList<Data>>()
        allProducts_initial.value= mutableListOf()

        dialog = Loading_dialog(context)
    }

    val exceptionHandler = CoroutineExceptionHandler { _, e ->
        dialog.hideLoadingDialog()
        noInternetException.postValue(true)
        job.cancel()
    }

    @Throws(IOException::class)
    fun getProduct(barcode: String?) {
        loading.postValue(true)

        dialog.displayLoadingDialog()
        job = viewModelScope.launch(exceptionHandler+Dispatchers.Default) {
            runCatching {
                remoteRepository.getProduct(barcode ?: "0002")
            }.onFailure {
                error.value= true
                loading.postValue(false)
                dialog.hideLoadingDialog()


            }.onSuccess {
               dialog.hideLoadingDialog()
               if(it.isSuccessful){
                   if(it.body()!!.success == 1 )
                      addProductToList(it.body()!!.data)
               }else{
                    loading.postValue(false)

                   if(it.code() == 404){
                       productNotFound.postValue(true)
                   } else{
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
            val tempList=allProducts.value!!
            tempList.add(product)
            allProducts.postValue(tempList)

            viewModelScope.launch(Dispatchers.Default) {
                allProducts_initial.postValue(tempList)
            }

        }else{
            productExists.postValue(true)
        }

    }

    fun submitProduct(product: SubmittedProduct) {
        dialog.displayLoadingDialog()

       job= viewModelScope.launch(exceptionHandler+Dispatchers.Default) {
            runCatching {
                remoteRepository.submitProduct(product)
            }.onFailure {

                error.value= true
                loading.postValue(false)
                dialog.hideLoadingDialog()


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
           val product = allProducts.value?.get(position)
           product?.let {
               allProducts.value?.remove(it)
               allProducts.notifyObserver()
           }

    }

    fun deleteAllProducts() {
        allProducts.value?.clear()
        allProducts.notifyObserver()
    }


    fun submitAllProducts(CardsShown: Boolean,productsSubmitted: (() -> Unit)?=null) {
        dialog.displayLoadingDialog()
        println("carddss $CardsShown")
        viewModelScope.launch(exceptionHandler+Dispatchers.Default) {
            runCatching {
                remoteRepository.submitProducts(SubmittedProducts(getSubmittedProducts(CardsShown),username!!, milliToDate(Calendar.getInstance().timeInMillis.toString())))
            }.onFailure {

                error.value= true
                loading.postValue(false)
                dialog.hideLoadingDialog()

            }.onSuccess {
                loading.postValue(false)
                dialog.hideLoadingDialog()
                productsSubmitted?.invoke()
//                if(it.isSuccessful){
//                    if(it.body()!!.success == 1 )
//                        updated.postValue(true)
//                }else{
//                    error.postValue(true)
//                }
            }
        }
    }

    private suspend fun getSubmittedProducts(CardsShown: Boolean): MutableList<Data1> {
        val list_to_be_saved= mutableListOf<Data1>()

        if(CardsShown){
            allProducts.value!!.forEach {
                list_to_be_saved.add(Data1(it.id,it.price,it.quantity,null,it.sku,it.status))
            }

        }else{
            allProducts.value!!.forEachIndexed { index, data ->
                val initial_quantity_value= allProducts_initial.value?.get(index)?.quantity
                val new_value= allProducts.value?.get(index)?.quantity

                val saved_quantity=initial_quantity_value?.toInt()!! -  data.quantity_minus?.toInt()!!

                list_to_be_saved.add(Data1(data.id,data.price,saved_quantity.toString(),saved_quantity.toString(),data.sku,data.status))
            }

        }
        return list_to_be_saved
    }


}



