package com.frag.eshophandling.di

import com.frag.eshophandling.ui.login.LoginActivity
import dagger.Subcomponent
import javax.inject.Singleton

@LoginActivityScope
@Subcomponent(modules = [ViewModelModule1::class])
interface LoginComponent {

    @Subcomponent.Factory
    interface Factory {
        fun create(): LoginComponent
    }

    fun inject(activity: LoginActivity)

}