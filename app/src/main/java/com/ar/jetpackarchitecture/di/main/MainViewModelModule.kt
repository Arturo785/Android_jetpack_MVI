package com.ar.jetpackarchitecture.di.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ar.jetpackarchitecture.di.auth.keys.MainViewModelKey
import com.ar.jetpackarchitecture.ui.main.account.AccountViewModel
import com.ar.jetpackarchitecture.ui.main.blog.viewmodel.BlogViewModel
import com.ar.jetpackarchitecture.ui.main.create_blog.CreateBlogViewModel
import com.ar.jetpackarchitecture.viewmodels.MainViewModelFactory
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class MainViewModelModule {


    @MainScope
    @Binds
    abstract fun provideViewModelFactory(factory : MainViewModelFactory) : ViewModelProvider.Factory

    @MainScope
    @Binds
    @IntoMap
    @MainViewModelKey(AccountViewModel::class)
    abstract fun bindAccountViewModel(accountViewModel: AccountViewModel): ViewModel


    @MainScope
    @Binds
    @IntoMap
    @MainViewModelKey(BlogViewModel::class)
    abstract fun bindBlogViewModel(blogViewModel: BlogViewModel): ViewModel

    @MainScope
    @Binds
    @IntoMap
    @MainViewModelKey(CreateBlogViewModel::class)
    abstract fun bindCreateBlogViewModel(createBlogViewModel: CreateBlogViewModel): ViewModel


}