package com.ar.jetpackarchitecture.repository.auth

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.switchMap
import com.ar.jetpackarchitecture.api.auth.OpenAPIAuthService
import com.ar.jetpackarchitecture.api.auth.network_responses.LoginResponse
import com.ar.jetpackarchitecture.api.auth.network_responses.RegistrationResponse
import com.ar.jetpackarchitecture.models.AccountProperties
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
    val sessionManager: SessionManager,
    val sharedPreferences: SharedPreferences,
    val sharedPreferencesEditor: SharedPreferences.Editor
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
            sessionManager.isConnectedToInternet(),
            true
        ){
            // not used in here
            override suspend fun createCacheRequestAndReturn() {

            }

            override suspend fun handleAPISuccessResponse(response: ApiSuccessResponse<LoginResponse>) {
                if(response.body.response == GENERIC_AUTH_ERROR){
                    return onErrorReturn(response.body.errorMessage, true, false)
                }

                // insert if does not exists because of foreign key relationship
                accountPropertiesDAO.insertOrIgnore(
                    AccountProperties(
                        response.body.pk,
                        response.body.email,
                        "" // does not matter
                    )
                )

                // will return -1 if failure
                val result = authTokenDAO.insert(
                    AuthToken(
                        response.body.pk,
                        response.body.token
                    )
                )

                if(result < 0){
                    return onCompleteJob(
                        DataState.error(
                            Response(ERROR_SAVE_AUTH_TOKEN, ResponseType.Dialog)
                        )
                    )
                }

                saveAuthenticatedUserToPrefs(email)

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
            sessionManager.isConnectedToInternet(),
            true
        ){

            // not used in here
            override suspend fun createCacheRequestAndReturn() {

            }

            override suspend fun handleAPISuccessResponse(response: ApiSuccessResponse<RegistrationResponse>) {
                if(response.body.response == GENERIC_AUTH_ERROR){
                    return onErrorReturn(response.body.errorMessage, true, false)
                }

                // insert if does not exists because of foreign key relationship
                accountPropertiesDAO.insertOrIgnore(
                    AccountProperties(
                        response.body.pk,
                        response.body.email,
                        "" // does not matter
                    )
                )

                // will return -1 if failure
                val result = authTokenDAO.insert(
                    AuthToken(
                        response.body.pk,
                        response.body.token
                    )
                )

                if(result < 0){
                    return onCompleteJob(
                        DataState.error(
                            Response(ERROR_SAVE_AUTH_TOKEN, ResponseType.Dialog)
                        )
                    )
                }

                saveAuthenticatedUserToPrefs(email)

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

    private fun saveAuthenticatedUserToPrefs(email: String) {
        sharedPreferencesEditor.putString(PreferenceKeys.PREVIOUS_AUTH_USER, email)
        sharedPreferencesEditor.apply()
    }


     fun checkPreviousAuthUser() : LiveData<DataState<AuthViewState>>{
        val previousAuthUserEmail : String? =
            sharedPreferences.getString(PreferenceKeys.PREVIOUS_AUTH_USER, null)

        if(previousAuthUserEmail.isNullOrBlank()){
            Log.d(TAG, "checkPreviousAuthUser: No Prev user Auth")
            return returnNoTokenFound()
        }

        return object : NetworkBoundResource<Void, AuthViewState>(
            sessionManager.isConnectedToInternet(),
            false
        ){
            // not used in this case
            override suspend fun handleAPISuccessResponse(response: ApiSuccessResponse<Void>) {

            }

            // not used in this case
            override fun createCall(): LiveData<GenericApiResponse<Void>> {
                return AbsentLiveData.create()
            }

            override fun setJob(job: Job) {
                repositoryJob?.cancel()
                repositoryJob = job
            }

            override suspend fun createCacheRequestAndReturn() {
                accountPropertiesDAO.searchByEmail(previousAuthUserEmail).let { accountProperties ->
                    Log.d(TAG, "createCacheRequestAndReturn: Search for token ${accountProperties}")

                    accountProperties?.let {
                        if(accountProperties.pk > -1){
                            authTokenDAO.searchByPk(accountProperties.pk).let { authToken ->
                                if(authToken != null){
                                    onCompleteJob(
                                        DataState.data(
                                            data = AuthViewState(
                                                authToken = authToken
                                            )
                                        )
                                    )
                                    return
                                }
                            }
                        }
                    }

                    // if account properties are null or something goes wrong
                    Log.d(TAG, "createCacheRequestAndReturn: AuthToken not found")
                    onCompleteJob(
                        DataState.data(
                            response = Response(
                                SuccessHandling.RESPONSE_CHECK_PREVIOUS_AUTH_USER_DONE,
                                ResponseType.None
                            )
                        )
                    )

                }
            }

        }.asLiveData()

    }

    private fun returnNoTokenFound(): LiveData<DataState<AuthViewState>> {
        return object : LiveData<DataState<AuthViewState>>(){

            override fun onActive() {
                super.onActive()
                value = DataState.data(
                    data = null,
                    response = Response(SuccessHandling.RESPONSE_CHECK_PREVIOUS_AUTH_USER_DONE, ResponseType.None)
                )
            }
        }
    }

}