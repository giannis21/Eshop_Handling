package com.frag.alertlocation_kotlin.utils

import android.content.SharedPreferences

object Preferences {
    var sharedPref : SharedPreferences?=null

    var token: String?
        get() {
            return sharedPref?.getString("token", "")
        }
        set(value) {
            sharedPref?.edit()?.putString("token", value)?.apply()
        }

    var BaseUrl: String?
        get() {
            return sharedPref?.getString("baseurl", "https://www.status-spar1ta.gr/")
        }
        set(value) {
            sharedPref?.edit()?.putString("baseurl", value)?.apply()
        }

    var useBearer: Boolean?
        get() {
            return sharedPref?.getBoolean("useBearer", false)
        }
        set(value) {
            sharedPref?.edit()?.putBoolean("useBearer", value!!)?.apply()
        }

    var username: String?
        get() {
            return sharedPref?.getString("username", "")
        }
        set(value) {
            sharedPref?.edit()?.putString("username", value!!)?.apply()
        }

    var password: String?
        get() {
            return sharedPref?.getString("password", "")
        }
        set(value) {
            sharedPref?.edit()?.putString("password", value!!)?.apply()
        }

    var lastLoginDate: String?
        get() {
            return sharedPref?.getString("lastLoginDate", "2010-11-11 13-18")
        }
        set(value) {
            sharedPref?.edit()?.putString("lastLoginDate", value!!)?.apply()
        }
}