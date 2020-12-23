package com.ar.jetpackarchitecture.ui.auth

import androidx.lifecycle.ViewModel
import com.ar.jetpackarchitecture.repository.auth.AuthRepository
import javax.inject.Inject

class AuthViewModel @Inject constructor(
    val authRepository: AuthRepository
) : ViewModel()
{

}