package com.ar.jetpackarchitecture.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.ar.jetpackarchitecture.api.auth.network_responses.LoginResponse
import com.ar.jetpackarchitecture.api.auth.network_responses.RegistrationResponse
import com.ar.jetpackarchitecture.repository.auth.AuthRepository
import com.ar.jetpackarchitecture.util.GenericApiResponse
import javax.inject.Inject

class AuthViewModel @Inject constructor(
    val authRepository: AuthRepository
) : ViewModel()
{

    fun testLoginRequest(email: String, password : String) : LiveData<GenericApiResponse<LoginResponse>> {
        return authRepository.testLoginRequest(email,password)
    }

    fun testRegistrationRequest(email: String,usename : String,  password : String, password2: String) : LiveData<GenericApiResponse<RegistrationResponse>> {
        return authRepository.testRegistrationRequest(email,usename,password,password2)
    }

}