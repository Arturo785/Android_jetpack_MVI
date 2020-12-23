package com.ar.jetpackarchitecture.repository.auth

import com.ar.jetpackarchitecture.api.auth.OpenAPIAuthService
import com.ar.jetpackarchitecture.persistence.AccountPropertiesDAO
import com.ar.jetpackarchitecture.persistence.AuthTokenDAO
import com.ar.jetpackarchitecture.session.SessionManager
import javax.inject.Inject

class AuthRepository @Inject constructor(
    val authTokenDAO: AuthTokenDAO,
    val accountPropertiesDAO: AccountPropertiesDAO,
    val openAPIAuthService: OpenAPIAuthService,
    val sessionManager: SessionManager
)
{

}