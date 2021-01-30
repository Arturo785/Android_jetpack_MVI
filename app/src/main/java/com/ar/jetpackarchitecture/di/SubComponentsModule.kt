package com.ar.jetpackarchitecture.di

import com.ar.jetpackarchitecture.di.auth.AuthComponent
import com.ar.jetpackarchitecture.di.main.MainComponent
import dagger.Module

@Module(
    subcomponents = [
        AuthComponent::class,
        MainComponent::class
    ]
)
class SubComponentsModule