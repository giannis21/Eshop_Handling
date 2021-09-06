package com.frag.eshophandling

import android.app.Application

import com.frag.eshophandling.di.AppComponent
import com.frag.eshophandling.di.DaggerAppComponent

open class MyApplication : Application() {


    // Instance of the AppComponent that will be used by the mainActivity in the project
    val appComponent: AppComponent by lazy {

        // Creates an instance of AppComponent using its Factory constructor
        // i pass the applicationContext that will be used as Context in the graph
        DaggerAppComponent.factory().create(applicationContext)

    }

}
