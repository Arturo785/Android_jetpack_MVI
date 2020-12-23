package com.ar.jetpackarchitecture.di

import androidx.lifecycle.ViewModelProvider
import com.ar.jetpackarchitecture.viewmodels.ViewModelProviderFactory
import dagger.Binds
import dagger.Module

@Module
abstract class ViewModelFactoryModule {

    // provides the factory
    @Binds
    abstract fun bindViewModelFactory(factory: ViewModelProviderFactory): ViewModelProvider.Factory
}