package com.frag.eshophandling.di

import com.frag.eshophandling.ui.login.LoginActivity
import dagger.Subcomponent
import javax.inject.Singleton


@Subcomponent
interface LoginComponent {
    // Factory to create instances of RegistrationComponent
    @Subcomponent.Factory
    interface Factory {
        fun create(): LoginComponent
    }

    fun inject(activity: LoginActivity)

}