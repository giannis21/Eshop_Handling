package com.frag.eshophandling.utils

import android.content.Context
import com.frag.alertlocation_kotlin.utils.Preferences
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DatastoreImpl @Inject constructor(var context: Context): Datastore {

    val sharedPref= context.applicationContext.getSharedPreferences("sharedPref", Context.MODE_PRIVATE)


    override fun setPassword(pass: String) {
        sharedPref?.edit()?.putString("password", pass)?.apply()
    }
    override fun getPassword():String  {
        return sharedPref?.getString("password", "") ?: ""
    }

    override fun setToken(token: String) {
        sharedPref?.edit()?.putString("token", token)?.apply()
    }

    override fun getToken():String {
        return sharedPref.getString("token", "") ?: ""
    }

    override fun setBaseUrl(value: String) {
        sharedPref?.edit()?.putString("baseurl", value)?.apply()
    }

    override fun getBaseUrl():String  {
        return sharedPref?.getString("baseurl", "https://www.status-sparta.gr/") ?: ""
    }

    override fun setLastLoginDate(value: String) {
        sharedPref?.edit()?.putString("lastLoginDate", value!!)?.apply()
    }

    override fun setUsername(username: String) {
        sharedPref?.edit()?.putString("username", username)?.apply()
    }

    override fun getUsername():String  {
        return sharedPref?.getString("username", "") ?:""
    }

    override fun getUseBearer():Boolean  {
        return sharedPref?.getBoolean("useBearer", false) ?: false
    }

    override fun setUseBearer(value: Boolean) {
        sharedPref?.edit()?.putBoolean("useBearer", value)?.apply()
    }

    override fun getLastLoginDate():String  {
        return sharedPref?.getString("lastLoginDate", "2010-11-11 13-18") ?: "2010-11-11 13-18"
    }
}