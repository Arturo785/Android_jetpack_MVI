package com.ar.jetpackarchitecture.di

import com.ar.jetpackarchitecture.di.auth.AuthFragmentBuildersModule
import com.ar.jetpackarchitecture.di.auth.AuthModule
import com.ar.jetpackarchitecture.di.auth.AuthScope
import com.ar.jetpackarchitecture.di.auth.AuthViewModelModule
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

    @ContributesAndroidInjector
    abstract fun contributeMainActivity(): MainActivity

}