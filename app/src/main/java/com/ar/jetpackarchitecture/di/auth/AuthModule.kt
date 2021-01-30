package com.ar.jetpackarchitecture.di.auth

import android.content.SharedPreferences
import com.ar.jetpackarchitecture.api.auth.OpenAPIAuthService
import com.ar.jetpackarchitecture.persistence.AccountPropertiesDAO
import com.ar.jetpackarchitecture.persistence.AuthTokenDAO
import com.ar.jetpackarchitecture.repository.auth.AuthRepository
import com.ar.jetpackarchitecture.repository.auth.AuthRepositoryImpl
import com.ar.jetpackarchitecture.session.SessionManager
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.FlowPreview
import retrofit2.Retrofit

@Module
object AuthModule{

    // TEMPORARY
    @JvmStatic
    @AuthScope
    @Provides
    fun provideFakeApiService(retrofitBuilder: Retrofit.Builder): OpenAPIAuthService{
        return retrofitBuilder
            .build()
            .create(OpenAPIAuthService::class.java)
    }

    @FlowPreview
    @JvmStatic
    @AuthScope
    @Provides
    fun provideAuthRepository(
        sessionManager: SessionManager,
        authTokenDao: AuthTokenDAO,
        accountPropertiesDao: AccountPropertiesDAO,
        openApiAuthService: OpenAPIAuthService,
        sharedPreferences: SharedPreferences,
        sharedPreferencesEditor: SharedPreferences.Editor
    ): AuthRepository {
        return AuthRepositoryImpl(
            authTokenDao,
            accountPropertiesDao,
            openApiAuthService,
            sessionManager,
            sharedPreferences,
            sharedPreferencesEditor
        )
    }

}