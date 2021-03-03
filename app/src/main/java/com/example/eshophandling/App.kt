package com.example.eshophandling

import android.app.Application
import android.content.Context
import com.example.alertlocation_kotlin.utils.Preferences

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        Preferences.sharedPref = this.getSharedPreferences("sharedPref", Context.MODE_PRIVATE)
    }
}