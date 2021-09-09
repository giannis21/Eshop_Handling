package com.frag.eshophandling.di

import android.content.Context
import com.frag.eshophandling.MainActivity
import com.frag.eshophandling.ui.ScannerFragment
import com.frag.eshophandling.ui.SettingsFragment
import com.frag.eshophandling.ui.cards.CardsFragment
import com.frag.eshophandling.ui.login.LoginActivity
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [NetworkModuleBasicAuth::class,NetworkModule::class,FactoryModule::class,AppSubcomponents::class])
interface AppComponent {
    @Component.Factory
    interface Factory {
        // With @BindsInstance, the Context passed in will be available in the graph
        fun create(@BindsInstance context: Context): AppComponent
    }

    fun loginComponent(): LoginComponent.Factory
    fun mainComponent(): MainComponent.Factory

//
//    fun inject(activity: MainActivity)
//    fun inject(cardsFragment: CardsFragment)
//    fun inject(scannerFragment: ScannerFragment)
//    fun inject(settingsFragment: SettingsFragment)
}