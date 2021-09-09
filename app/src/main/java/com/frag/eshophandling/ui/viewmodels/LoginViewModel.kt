package com.frag.eshophandling.ui.viewmodels

import android.content.Context
import android.util.Base64
import androidx.lifecycle.MutableLiveData

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.frag.alertlocation_kotlin.utils.Preferences
import com.frag.alertlocation_kotlin.utils.Preferences.token
import com.frag.alertlocation_kotlin.utils.Preferences.useBearer
import com.frag.eshophandling.data.api.NoInternetException
import com.frag.eshophandling.data.api.RemoteRepository
import com.frag.eshophandling.data.model.loginResponse.login.LoginUser
import com.frag.eshophandling.di.LoginActivityScope
import com.frag.eshophandling.utils.Datastore
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.UnsupportedEncodingException
import java.net.UnknownHostException
import javax.inject.Inject
import javax.inject.Singleton
import javax.net.ssl.SSLPeerUnverifiedException

@Singleton
class LoginViewModel @Inject constructor(var remoteRepository: RemoteRepository, var context: Context,var datastore: Datastore) : ViewModel() {

    var LoggedIn = MutableLiveData<Boolean>(false)
    var token1 =MutableLiveData<String>("")
    var validUrl = MutableLiveData<String>("")
    var loading = MutableLiveData<Boolean>(false)
    var noInternetException = MutableLiveData<Boolean>(false)
    var error = MutableLiveData<Boolean>()
    var job:Job=Job()
    var unknownHostException=MutableLiveData<Boolean>()
    var invalid_creds=MutableLiveData<Boolean>()
    val exceptionHandler = CoroutineExceptionHandler { _, e ->

        noInternetException.postValue(true)
        job.cancel()
    }
    fun getCredentials(username: String, password: String,url: String) {
        Preferences.sharedPref = context.getSharedPreferences("sharedPref", Context.MODE_PRIVATE)
        loading.postValue(true)

        viewModelScope.launch(exceptionHandler + Dispatchers.Default) {
            runCatching {
                datastore.setUseBearer(false)
                remoteRepository.getCredentials(getAuthToken(),url)
            }.onFailure {
                loading.postValue(false)

                if(it is NoInternetException){
                    noInternetException.postValue(true)
                }else if( it is UnknownHostException || it is SSLPeerUnverifiedException){
                    unknownHostException.postValue(true)
                }


            }.onSuccess {
                loading.postValue(false)

                 if(it.isSuccessful){
                     if(it.body()?.success == 1){

                         datastore.setToken(it.body()!!.data.access_token)
                         datastore.setUsername(username)
                         datastore.setPassword(password)

                         login(username,password)
                     }
                 }else{
                     error.postValue(true)
                 }
            }
        }
    }


    fun login(username: String ="admin", password: String  ="qwe123!") {
        datastore.setUseBearer(true)
        viewModelScope.launch(exceptionHandler+Dispatchers.Default) {
            runCatching {
                val url = datastore.getBaseUrl()+"api/rest_admin/login"
                remoteRepository.login(LoginUser(username, password),url)
            }.onFailure {

                if(it is NoInternetException){
                    noInternetException.postValue(true)
                }

                datastore.setToken("")
                loading.postValue(false)

            }.onSuccess {
                loading.postValue(false)

                if(it.isSuccessful){
                    LoggedIn.postValue(true)
                }else{
                    if(it.code() == 401){
                        invalid_creds?.postValue(true)
                    }else{
                        error?.postValue(true)
                    }

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