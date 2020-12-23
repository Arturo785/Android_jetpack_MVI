package com.ar.jetpackarchitecture.session

import android.app.Application
import com.ar.jetpackarchitecture.persistence.AuthTokenDAO
import javax.inject.Inject
import javax.inject.Singleton

// used for dagger
@Singleton
class SessionManager @Inject constructor(
    val authTokenDAO: AuthTokenDAO,
    val application: Application
)
{

}