package com.frag.eshophandling.utils

interface Datastore {
    fun setUsername(username:String)
    fun setPassword(pass:String)
    fun setToken(token:String)
    fun setUseBearer(value:Boolean)
    fun setBaseUrl(value:String)
    fun setLastLoginDate(value:String)

    fun getUsername():String
    fun getPassword():String
    fun getToken():String
    fun getUseBearer():Boolean
    fun getBaseUrl():String
    fun getLastLoginDate():String
}