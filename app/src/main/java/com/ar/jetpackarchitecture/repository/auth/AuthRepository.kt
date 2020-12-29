package com.ar.jetpackarchitecture.repository.auth

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.switchMap
import com.ar.jetpackarchitecture.api.auth.OpenAPIAuthService
import com.ar.jetpackarchitecture.api.auth.network_responses.LoginResponse
import com.ar.jetpackarchitecture.api.auth.network_responses.RegistrationResponse
import com.ar.jetpackarchitecture.models.AuthToken
import com.ar.jetpackarchitecture.persistence.AccountPropertiesDAO
import com.ar.jetpackarchitecture.persistence.AuthTokenDAO
import com.ar.jetpackarchitecture.repository.NetworkBoundResource
import com.ar.jetpackarchitecture.session.SessionManager
import com.ar.jetpackarchitecture.ui.Data
import com.ar.jetpackarchitecture.ui.DataState
import com.ar.jetpackarchitecture.ui.Response
import com.ar.jetpackarchitecture.ui.ResponseType
import com.ar.jetpackarchitecture.ui.auth.state.AuthViewState
import com.ar.jetpackarchitecture.ui.auth.state.LoginFields
import com.ar.jetpackarchitecture.ui.auth.state.RegistrationFields
import com.ar.jetpackarchitecture.util.*
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.Job
import javax.inject.Inject

class AuthRepository @Inject constructor(
    val authTokenDAO: AuthTokenDAO,
    val accountPropertiesDAO: AccountPropertiesDAO,
    val openAPIAuthService: OpenAPIAuthService,
    val sessionManager: SessionManager
)
{
    var TAG = "AuthRepository"

    private var repositoryJob : Job? = null


    fun attemptLogin(email: String, password: String) : LiveData<DataState<AuthViewState>>{
        val loginFields = LoginFields(email, password).isValidForLogin()

        if(!loginFields.equals(LoginFields.LoginError.none())){
            // means that errors exists
            return returnErrorResponse(loginFields, ResponseType.Dialog)
        }

        return object : NetworkBoundResource<LoginResponse, AuthViewState>(
            sessionManager.isConnectedToInternet()
        ){
            override suspend fun handleAPISuccessResponse(response: ApiSuccessResponse<LoginResponse>) {
                if(response.body.response == GENERIC_AUTH_ERROR){
                    return onErrorReturn(response.body.errorMessage, true, false)
                }

                onCompleteJob(
                    DataState.data(
                        data = AuthViewState(
                            authToken = AuthToken(response.body.pk, response.body.token)
                        )
                    )
                )
            }

            override fun createCall(): LiveData<GenericApiResponse<LoginResponse>> {
                return openAPIAuthService.login(email, password)
            }

            override fun setJob(job: Job) {
                repositoryJob?.cancel() // if previous job exists cancels it
                repositoryJob = job
            }

        }.asLiveData()
    }

    private fun returnErrorResponse(errorMessge: String, responseType: ResponseType): LiveData<DataState<AuthViewState>> {
        return object : LiveData<DataState<AuthViewState>>(){
            override fun onActive() {
                super.onActive()
                value = DataState.error(
                    Response(
                        errorMessge,
                        responseType
                    )
                )
            }
        }
    }

    fun cancelActiveJobs(){
        Log.d(TAG, "cancelling all jobs: ")
        repositoryJob?.cancel()
    }


    fun attemptLoginfake(email : String, password : String) : LiveData<DataState<AuthViewState>>{
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

    fun attemptRegistrationFake(email : String, username : String, password : String, password2 : String) : LiveData<DataState<AuthViewState>>{
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


    fun attemptRegistration(email : String, username : String, password : String, password2 : String) : LiveData<DataState<AuthViewState>> {
        val registrationFieldsErrors = RegistrationFields(email, username, password, password2).isValidForRegistration()

        if(registrationFieldsErrors != RegistrationFields.RegistrationError.none()){
            // there are errors
            return returnErrorResponse(registrationFieldsErrors, ResponseType.Dialog)
        }

        return object : NetworkBoundResource<RegistrationResponse, AuthViewState>(
            sessionManager.isConnectedToInternet()
        ){
            override suspend fun handleAPISuccessResponse(response: ApiSuccessResponse<RegistrationResponse>) {
                if(response.body.response == GENERIC_AUTH_ERROR){
                    return onErrorReturn(response.body.errorMessage, true, false)
                }

                onCompleteJob(
                    DataState.data(
                        data = AuthViewState(
                            authToken = AuthToken(response.body.pk, response.body.token)
                        )
                    )
                )
            }

            override fun createCall(): LiveData<GenericApiResponse<RegistrationResponse>> {
                return openAPIAuthService.register(email, username, password, password2)
            }

            override fun setJob(job: Job) {
                repositoryJob?.cancel() // if previous job exists cancels it
                repositoryJob = job
            }

        }.asLiveData()
    }

}