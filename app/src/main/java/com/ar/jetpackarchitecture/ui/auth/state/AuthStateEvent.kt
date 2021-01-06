package com.ar.jetpackarchitecture.ui.auth.state

sealed class AuthStateEvent {

    data class LoginAttemptEvent(val email : String, val password : String) : AuthStateEvent()

    data class RegisterAttemptEvent(
        val email : String,
        val username : String,
        val password : String,
        val password2 : String
    ) : AuthStateEvent()

    object CheckPreviousAuthEvent : AuthStateEvent()

    object None : AuthStateEvent()

}