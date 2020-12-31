package com.ar.jetpackarchitecture.di

import com.ar.jetpackarchitecture.di.auth.AuthFragmentBuildersModule
import com.ar.jetpackarchitecture.di.auth.AuthModule
import com.ar.jetpackarchitecture.di.auth.AuthScope
import com.ar.jetpackarchitecture.di.auth.AuthViewModelModule
import com.ar.jetpackarchitecture.di.main.MainFragmentBuildersModule
import com.ar.jetpackarchitecture.di.main.MainModule
import com.ar.jetpackarchitecture.di.main.MainScope
import com.ar.jetpackarchitecture.di.main.MainViewModelModule
import com.ar.jetpackarchitecture.ui.auth.AuthActivity
import com.ar.jetpackarchitecture.ui.main.MainActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBuildersModule {

    @AuthScope
    @ContributesAndroidInjector(
        modules = [AuthModule::class, AuthFragmentBuildersModule::class, AuthViewModelModule::class]
    )
    abstract fun contributeAuthActivity(): AuthActivity

    @MainScope
    @ContributesAndroidInjector(
        modules = [MainModule::class, MainFragmentBuildersModule::class, MainViewModelModule::class]
    )
    abstract fun contributeMainActivity(): MainActivity

}