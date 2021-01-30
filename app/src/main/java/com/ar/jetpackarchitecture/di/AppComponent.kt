package com.ar.jetpackarchitecture.di

import android.app.Application
import com.ar.jetpackarchitecture.BaseApplication
import com.ar.jetpackarchitecture.di.auth.AuthComponent
import com.ar.jetpackarchitecture.di.main.MainComponent
import com.ar.jetpackarchitecture.session.SessionManager
import com.ar.jetpackarchitecture.ui.BaseActivity
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AppModule::class,
    SubComponentsModule::class
    ]
)
interface AppComponent {

    // is in here because it can be injected everywhere
    val sessionManager: SessionManager // must add here b/c injecting into abstract class

    @Component.Builder
    interface Builder{

        @BindsInstance
        fun application(application: Application): Builder

        fun build(): AppComponent
    }


    // with this gives access to all the things in here like the modules, the sessionManager and
    // application etc
    fun inject(baseActivity: BaseActivity)

    // is in here because it can be injected everywhere
    fun authComponent(): AuthComponent.Factory

    fun mainComponent(): MainComponent.Factory
}