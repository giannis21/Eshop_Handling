package com.frag.eshophandling.utils

interface Datastore {
    fun addUsername(username:String)
    fun addPassword(pass:String)
    fun addToken(token:String)
    fun addUseBearer(value:Boolean)
    fun addBaseUrl(value:String)
    fun setLastLoginDate(value:String)

    fun getUsername():String
    fun getPassword():String
    fun getToken():String
    fun getUseBearer():Boolean
    fun getBaseUrl():String
    fun getLastLoginDate():String
}