package com.frag.eshophandling.utils

import android.content.Context
import com.frag.alertlocation_kotlin.utils.Preferences
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DatastoreImpl @Inject constructor(var context: Context): Datastore {

    val sharedPref= context.applicationContext.getSharedPreferences("sharedPref", Context.MODE_PRIVATE)


    override fun addPassword(pass: String) {
        sharedPref?.edit()?.putString("password", pass!!)?.apply()
    }
    override fun getPassword():String  {
        return sharedPref?.getString("password", "") ?: ""
    }

    override fun addToken(token: String) {
        sharedPref?.edit()?.putString("token", token)?.apply()
    }

    override fun getToken():String {
        return sharedPref.getString("token", "") ?: ""
    }

    override fun addBaseUrl(value: String) {
        sharedPref?.edit()?.putString("baseurl", value)?.apply()
    }

    override fun getBaseUrl():String  {
        return sharedPref?.getString("baseurl", "https://www.status-spar1ta.gr/") ?: ""
    }

    override fun setLastLoginDate(value: String) {
        sharedPref?.edit()?.putString("lastLoginDate", value!!)?.apply()
    }

    override fun addUsername(username: String) {
        sharedPref?.edit()?.putString("username", username)?.apply()
    }

    override fun getUsername():String  {
        return sharedPref?.getString("username", "") ?:""
    }

    override fun getUseBearer():Boolean  {
        return sharedPref?.getBoolean("useBearer", false) ?: false
    }

    override fun addUseBearer(value: Boolean) {
        sharedPref?.edit()?.putBoolean("useBearer", value)?.apply()
    }

    override fun getLastLoginDate():String  {
        return sharedPref?.getString("lastLoginDate", "2010-11-11 13-18") ?: "2010-11-11 13-18"
    }
}