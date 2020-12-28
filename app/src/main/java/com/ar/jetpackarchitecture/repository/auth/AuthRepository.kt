package com.ar.jetpackarchitecture.repository.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.switchMap
import com.ar.jetpackarchitecture.api.auth.OpenAPIAuthService
import com.ar.jetpackarchitecture.api.auth.network_responses.LoginResponse
import com.ar.jetpackarchitecture.api.auth.network_responses.RegistrationResponse
import com.ar.jetpackarchitecture.models.AuthToken
import com.ar.jetpackarchitecture.persistence.AccountPropertiesDAO
import com.ar.jetpackarchitecture.persistence.AuthTokenDAO
import com.ar.jetpackarchitecture.session.SessionManager
import com.ar.jetpackarchitecture.ui.DataState
import com.ar.jetpackarchitecture.ui.Response
import com.ar.jetpackarchitecture.ui.ResponseType
import com.ar.jetpackarchitecture.ui.auth.state.AuthViewState
import com.ar.jetpackarchitecture.util.*
import javax.inject.Inject

class AuthRepository @Inject constructor(
    val authTokenDAO: AuthTokenDAO,
    val accountPropertiesDAO: AccountPropertiesDAO,
    val openAPIAuthService: OpenAPIAuthService,
    val sessionManager: SessionManager
)
{

    fun attemptLogin(email : String, password : String) : LiveData<DataState<AuthViewState>>{
        return openAPIAuthService.login(email,password)
            .switchMap { response ->
                object : LiveData<DataState<AuthViewState>>(){
                    override fun onActive() {
                        super.onActive()

                        // uses the builders inside the class
                        when(response){

                            is ApiSuccessResponse -> {
                                 value = DataState.data(
                                     AuthViewState(
                                         authToken = AuthToken(
                                             response.body.pk,
                                             response.body.token
                                         )
                                     ),
                                     response = null
                                 )
                            }

                            is ApiErrorResponse -> {

                                value = DataState.error(
                                    response = Response(
                                        message = response.errorMessage,
                                        responseType = ResponseType.Dialog
                                    )
                                )

                            }

                            is ApiEmptyResponse -> {

                                value = DataState.error(
                                    response = Response(
                                        message = ERROR_UNKNOWN,
                                        responseType = ResponseType.Dialog
                                    )
                                )

                            }

                        }

                    }
                }
            }
    }

    fun attemptRegistration(email : String, username : String, password : String, password2 : String) : LiveData<DataState<AuthViewState>>{
        return openAPIAuthService.register(email,username,password, password2)
            .switchMap { response ->
                object : LiveData<DataState<AuthViewState>>(){
                    override fun onActive() {
                        super.onActive()

                        // uses the builders inside the class
                        when(response){

                            is ApiSuccessResponse -> {
                                value = DataState.data(
                                    AuthViewState(
                                        authToken = AuthToken(
                                            response.body.pk,
                                            response.body.token
                                        )
                                    ),
                                    response = null
                                )
                            }

                            is ApiErrorResponse -> {

                                value = DataState.error(
                                    response = Response(
                                        message = response.errorMessage,
                                        responseType = ResponseType.Dialog
                                    )
                                )

                            }

                            is ApiEmptyResponse -> {

                                value = DataState.error(
                                    response = Response(
                                        message = ERROR_UNKNOWN,
                                        responseType = ResponseType.Dialog
                                    )
                                )

                            }

                        }

                    }
                }
            }
    }

}