package com.frag.eshophandling.ui.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.frag.eshophandling.data.api.RemoteRepository


@Suppress("UNCHECKED_CAST")
class ViewmodelFactory(private val remoteRepository: RemoteRepository, var context: Context) : ViewModelProvider.Factory{

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> LoginViewModel(remoteRepository,context) as T
            modelClass.isAssignableFrom(SharedViewModel::class.java) -> SharedViewModel(remoteRepository,context) as T

            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }

    }
}

