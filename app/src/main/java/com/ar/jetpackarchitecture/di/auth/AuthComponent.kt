package com.ar.jetpackarchitecture.di.auth

import com.ar.jetpackarchitecture.di.AuthFragmentsModule
import com.ar.jetpackarchitecture.ui.auth.AuthActivity
import dagger.Subcomponent

@AuthScope
@Subcomponent(
    modules = [
        AuthModule::class,
        AuthViewModelModule::class,
        AuthFragmentsModule::class
    ])
interface AuthComponent {

    @Subcomponent.Factory
    interface Factory{

        fun create(): AuthComponent
    }

    fun inject(authActivity: AuthActivity)

}