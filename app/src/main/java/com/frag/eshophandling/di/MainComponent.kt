package com.frag.eshophandling.di

import android.content.Context
import com.frag.eshophandling.MainActivity
import com.frag.eshophandling.ui.ScannerFragment
import com.frag.eshophandling.ui.SettingsFragment
import com.frag.eshophandling.ui.cards.CardsFragment
import com.frag.eshophandling.ui.login.LoginActivity
import dagger.BindsInstance
import dagger.Component
import dagger.Subcomponent
import javax.inject.Singleton

//@MainActivityScope
//@Component( dependencies = [AppComponent::class],modules = [ViewModelModule1::class])
//interface MainComponent {
//    // Factory to create instances of RegistrationComponent
//    @Subcomponent.Factory
//    interface Factory {
//        //fun create(): MainComponent
//      //  fun build(): AppComponent
//        fun create(@BindsInstance context: Context): MainComponent
//     //   fun appComp(appComponent: AppComponent): Factory
//    }
//
//
//    fun inject(activity: MainActivity)
//    fun inject(cardsFragment: CardsFragment)
//    fun inject(settingsFragment: SettingsFragment)
//    fun inject(scannerFragment: ScannerFragment)
//}