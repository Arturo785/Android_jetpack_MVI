package com.ar.jetpackarchitecture.di.main

import com.ar.jetpackarchitecture.api.main.OpenApiMainService
import com.ar.jetpackarchitecture.di.AppModule_ProvidesRetrofitBuilderFactory
import com.ar.jetpackarchitecture.persistence.AccountPropertiesDAO
import com.ar.jetpackarchitecture.repository.main.AccountRepository
import com.ar.jetpackarchitecture.session.SessionManager
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit

@Module
class MainModule {

    @MainScope
    @Provides
    fun provideOpenApiMainService(retrofitBuilder : Retrofit.Builder) : OpenApiMainService{
        return retrofitBuilder
            .build()
            .create(OpenApiMainService::class.java)
    }

    @MainScope
    @Provides
    fun provideMainAccountRepository(
        openApiMainService : OpenApiMainService,
        accountPropertiesDAO: AccountPropertiesDAO,
        sessionManager: SessionManager
    ) : AccountRepository{
        return AccountRepository(
            openApiMainService,
            accountPropertiesDAO,
            sessionManager
        )
    }

}