package com.ar.jetpackarchitecture.di.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ar.jetpackarchitecture.di.auth.keys.AuthViewModelKey
import com.ar.jetpackarchitecture.di.auth.keys.MainViewModelKey
import com.ar.jetpackarchitecture.ui.auth.AuthViewModel
import com.ar.jetpackarchitecture.viewmodels.AuthViewModelFactory
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class AuthViewModelModule {


    @AuthScope
    @Binds
    abstract fun bindViewModelFactory(factory : AuthViewModelFactory) : ViewModelProvider.Factory

    @AuthScope
    @Binds
    @IntoMap
    @AuthViewModelKey(AuthViewModel::class)
    abstract fun bindAuthViewModel(authViewModel: AuthViewModel): ViewModel

}