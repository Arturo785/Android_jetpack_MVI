package com.ar.jetpackarchitecture.di.auth

import com.ar.jetpackarchitecture.api.auth.OpenAPIAuthService
import com.ar.jetpackarchitecture.persistence.AccountPropertiesDAO
import com.ar.jetpackarchitecture.persistence.AuthTokenDAO
import com.ar.jetpackarchitecture.repository.auth.AuthRepository
import com.ar.jetpackarchitecture.session.SessionManager
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit

@Module
class AuthModule{

    // TEMPORARY
    @AuthScope
    @Provides
    fun provideFakeApiService(): OpenAPIAuthService{
        return Retrofit.Builder()
            .baseUrl("https://open-api.xyz")
            .build()
            .create(OpenAPIAuthService::class.java)
    }

    @AuthScope
    @Provides
    fun provideAuthRepository(
        sessionManager: SessionManager,
        authTokenDao: AuthTokenDAO,
        accountPropertiesDao: AccountPropertiesDAO,
        openApiAuthService: OpenAPIAuthService
    ): AuthRepository {
        return AuthRepository(
            authTokenDao,
            accountPropertiesDao,
            openApiAuthService,
            sessionManager
        )
    }

}