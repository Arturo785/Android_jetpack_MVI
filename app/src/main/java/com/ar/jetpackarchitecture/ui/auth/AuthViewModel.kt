package com.ar.jetpackarchitecture.ui.auth

import androidx.lifecycle.ViewModel
import com.ar.jetpackarchitecture.repository.auth.AuthRepository

class AuthViewModel constructor(
    val authRepository: AuthRepository
) : ViewModel()
{

}