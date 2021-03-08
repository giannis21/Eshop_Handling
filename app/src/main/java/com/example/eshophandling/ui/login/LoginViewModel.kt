package com.example.eshophandling.ui.login

import android.content.Context
import android.util.Base64
import androidx.lifecycle.MutableLiveData

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.alertlocation_kotlin.utils.Preferences
import com.example.alertlocation_kotlin.utils.Preferences.token
import com.example.alertlocation_kotlin.utils.Preferences.useBearer
import com.example.eshophandling.api.NoInternetException
import com.example.eshophandling.api.RemoteRepository
import com.example.eshophandling.ui.login.loginResponse.login.LoginUser
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.UnsupportedEncodingException


class LoginViewModel(var remoteRepository: RemoteRepository, var context: Context) : ViewModel() {

    var LoggedIn = MutableLiveData<Boolean>(false)
    var token1 =MutableLiveData<String>("")
    var validUrl = MutableLiveData<String>("")
    var loading = MutableLiveData<Boolean>(false)
    var noInternetException = MutableLiveData<Boolean>(false)
    var error :MutableLiveData<Boolean> ?=null
    var job:Job=Job()
    val exceptionHandler = CoroutineExceptionHandler { _, e ->

        noInternetException.postValue(true)
        job.cancel()
    }
    fun getCredentials(username: String, password: String,url: String) {
        Preferences.sharedPref = context.getSharedPreferences("sharedPref", Context.MODE_PRIVATE)
        loading.postValue(true)

        viewModelScope.launch(exceptionHandler+Dispatchers.Default) {
            runCatching {
                useBearer = false
                remoteRepository.getCredentials(getAuthToken())
            }.onFailure {

                if(it is NoInternetException){
                    noInternetException.postValue(true)
                }
                loading.postValue(false)

            }.onSuccess {

                 if(it.isSuccessful){
                     if(it.body()?.success == 1){
                         token1.postValue(it.body()!!.data.access_token)
                         token = it.body()!!.data.access_token
                         validUrl.postValue(url)
                         login(username,password)
                     }
                 }else{
                     loading.postValue(false)
                     println("user not Logged in ${it.errorBody()}")
                     error?.postValue(true)
                 }
            }
        }
    }


    fun login(username: String ="admin", password: String  ="admin") {
        useBearer = true
        viewModelScope.launch(exceptionHandler+Dispatchers.Default) {
            runCatching {
                remoteRepository.login(LoginUser("admin", "qwe123!"))
            }.onFailure {
                if(it is NoInternetException){
                    noInternetException.postValue(true)
                }
                println(it.message)
                token =""
                loading.postValue(false)
            }.onSuccess {
                loading.postValue(false)

                if(it.isSuccessful){
                    LoggedIn.postValue(true)
                }else{
                    error?.postValue(true)
                }

            }
        }
    }

    fun getAuthToken( username:String ="admin",  password: String  ="admin"): String {
        var data = ByteArray(0)
        try {
            data = ("$username:$password").toByteArray(charset("UTF-8"))
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }
        return "Basic " + Base64.encodeToString(data, Base64.NO_WRAP)
    }
}