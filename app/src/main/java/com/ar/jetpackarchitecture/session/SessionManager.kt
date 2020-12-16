package com.ar.jetpackarchitecture.session

import android.app.Application
import com.ar.jetpackarchitecture.persistence.AuthTokenDAO

// used for dagger
class SessionManager constructor(
    val authTokenDAO: AuthTokenDAO,
    val application: Application
)
{

}