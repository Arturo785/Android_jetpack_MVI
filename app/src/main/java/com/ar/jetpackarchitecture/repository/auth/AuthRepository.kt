package com.ar.jetpackarchitecture.repository.auth

import androidx.lifecycle.LiveData
import com.ar.jetpackarchitecture.api.auth.OpenAPIAuthService
import com.ar.jetpackarchitecture.api.auth.network_responses.LoginResponse
import com.ar.jetpackarchitecture.api.auth.network_responses.RegistrationResponse
import com.ar.jetpackarchitecture.persistence.AccountPropertiesDAO
import com.ar.jetpackarchitecture.persistence.AuthTokenDAO
import com.ar.jetpackarchitecture.session.SessionManager
import com.ar.jetpackarchitecture.util.GenericApiResponse
import javax.inject.Inject

class AuthRepository @Inject constructor(
    val authTokenDAO: AuthTokenDAO,
    val accountPropertiesDAO: AccountPropertiesDAO,
    val openAPIAuthService: OpenAPIAuthService,
    val sessionManager: SessionManager
)
{

    fun testLoginRequest(email: String, password : String) : LiveData<GenericApiResponse<LoginResponse>>{
        return openAPIAuthService.login(email,password)
    }

    fun testRegistrationRequest(email: String,usename : String,  password : String, password2: String) : LiveData<GenericApiResponse<RegistrationResponse>>{
        return openAPIAuthService.register(email,usename,password,password2)
    }

}