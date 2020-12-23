package com.ar.jetpackarchitecture.di.auth

import androidx.lifecycle.ViewModel
import com.ar.jetpackarchitecture.di.ViewModelKey
import com.ar.jetpackarchitecture.ui.auth.AuthViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class AuthViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(AuthViewModel::class)
    abstract fun bindAuthViewModel(authViewModel: AuthViewModel): ViewModel

}