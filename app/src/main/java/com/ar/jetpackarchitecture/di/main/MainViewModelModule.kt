package com.ar.jetpackarchitecture.di.main

import androidx.lifecycle.ViewModel
import com.ar.jetpackarchitecture.di.ViewModelKey
import com.ar.jetpackarchitecture.ui.main.account.AccountViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class MainViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(AccountViewModel::class)
    abstract fun bindAccountViewModel(accountViewModel: AccountViewModel): ViewModel



}