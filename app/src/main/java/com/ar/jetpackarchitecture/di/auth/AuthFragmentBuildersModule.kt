package com.ar.jetpackarchitecture.di.auth

import com.ar.jetpackarchitecture.ui.auth.ForgotPasswordFragment
import com.ar.jetpackarchitecture.ui.auth.LauncherFragment
import com.ar.jetpackarchitecture.ui.auth.LoginFragment
import com.ar.jetpackarchitecture.ui.auth.RegisterFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class AuthFragmentBuildersModule {

    @ContributesAndroidInjector()
    abstract fun contributeLauncherFragment(): LauncherFragment

    @ContributesAndroidInjector()
    abstract fun contributeLoginFragment(): LoginFragment

    @ContributesAndroidInjector()
    abstract fun contributeRegisterFragment(): RegisterFragment

    @ContributesAndroidInjector()
    abstract fun contributeForgotPasswordFragment(): ForgotPasswordFragment

}