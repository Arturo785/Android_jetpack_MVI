package com.ar.jetpackarchitecture.ui.auth.state

import com.ar.jetpackarchitecture.models.AuthToken


data class AuthViewState(
    var registrationFields : RegistrationFields? = RegistrationFields(),
    var loginFields : LoginFields? = LoginFields(),
    var authToken : AuthToken? = null
)


data class RegistrationFields(
    var registration_email : String? = null,
    var registration_username : String? = null,
    var registration_password : String? = null,
    var registration_password_2 : String? = null,
){
    class RegistrationError{
        companion object{

            fun mustFillAllFields() =
                "All fields are required"

            fun passwordsDoNotMatch() =
                "Passwords must match"

            fun none()=
                 "None"
        }
    }

    fun isValidForRegistration() : String{
        if(registration_email.isNullOrEmpty() ||
            registration_username.isNullOrEmpty() ||
            registration_password.isNullOrEmpty() ||
            registration_password_2.isNullOrEmpty()
        ){
            return RegistrationError.mustFillAllFields()
        }

        else if(!registration_password.equals(registration_password_2)){
            return RegistrationError.passwordsDoNotMatch()
        }

        return RegistrationError.none()
    }
}

data class LoginFields(
    var login_email: String? = null,
    var login_password: String? = null
){

    class LoginError {

        companion object{

            fun mustFillAllFields() =
                "You can't login without an email and password."

            fun none() =
                "None"
        }
    }

    fun isValidForLogin(): String{

        if(login_email.isNullOrEmpty()
            || login_password.isNullOrEmpty()){

            return LoginError.mustFillAllFields()
        }
        return LoginError.none()
    }

    override fun toString(): String {
        return "LoginState(email=$login_email, password=$login_password)"
    }
}