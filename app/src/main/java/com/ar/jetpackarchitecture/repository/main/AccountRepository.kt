package com.ar.jetpackarchitecture.repository.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.switchMap
import com.ar.jetpackarchitecture.api.GenericResponse
import com.ar.jetpackarchitecture.api.main.OpenApiMainService
import com.ar.jetpackarchitecture.models.AccountProperties
import com.ar.jetpackarchitecture.models.AuthToken
import com.ar.jetpackarchitecture.persistence.AccountPropertiesDAO
import com.ar.jetpackarchitecture.repository.JobManager
import com.ar.jetpackarchitecture.repository.NetworkBoundResource
import com.ar.jetpackarchitecture.session.SessionManager
import com.ar.jetpackarchitecture.ui.DataState
import com.ar.jetpackarchitecture.ui.Response
import com.ar.jetpackarchitecture.ui.ResponseType
import com.ar.jetpackarchitecture.ui.main.account.state.AccountViewState
import com.ar.jetpackarchitecture.util.AbsentLiveData
import com.ar.jetpackarchitecture.util.ApiSuccessResponse
import com.ar.jetpackarchitecture.util.GenericApiResponse
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.Job
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AccountRepository @Inject constructor(
    val openApiMainService: OpenApiMainService,
    val accountPropertiesDAO: AccountPropertiesDAO,
    val sessionManager: SessionManager
) : JobManager("AccountRepository"){

    val TAG = "AccountRepository"


    fun getAccountProperties(authToken : AuthToken) : LiveData<DataState<AccountViewState>>{
        return object : NetworkBoundResource<AccountProperties, AccountProperties, AccountViewState>(
            sessionManager.isConnectedToInternet(),
            true,
            true,
            false
        ){
            override suspend fun handleAPISuccessResponse(response: ApiSuccessResponse<AccountProperties>) {
                updateLocalDb(response.body)

                createCacheRequestAndReturn()
            }

            override fun createCall(): LiveData<GenericApiResponse<AccountProperties>> {
                return openApiMainService.getAccountProperties(
                    "Token ${authToken.token}"
                // that is how is required in the api
                )
            }

            override fun setJob(job: Job) {
                addJob("getAccountProperties", job)
            }

            // when network down
            override suspend fun createCacheRequestAndReturn() {
                withContext(Main){
                    result.addSource(loadFromCache()){ viewState ->
                        onCompleteJob(DataState.data(
                            data = viewState,
                            response = null
                        ))
                    }
                }
            }

            override fun loadFromCache(): LiveData<AccountViewState> {
                return accountPropertiesDAO.searchByPk(authToken.account_pk!!)
                    .switchMap {
                        object : LiveData<AccountViewState>(){

                            override fun onActive() {
                                super.onActive()
                                value = AccountViewState(it)
                            }
                        }
                    }
            }

            override suspend fun updateLocalDb(cachedObject: AccountProperties?) {
                cachedObject?.let {
                    accountPropertiesDAO.updateAccountProperties(
                        cachedObject.pk,
                        cachedObject.email,
                        cachedObject.username
                    )
                }
            }


        }.asLiveData()
    }

    fun saveAccountProperties(authToken: AuthToken, accountProperties: AccountProperties): LiveData<DataState<AccountViewState>> {
        return object : NetworkBoundResource<GenericResponse, Any, AccountViewState>(
            sessionManager.isConnectedToInternet(),
            isNetworkRequest = true,
            shouldLoadFromCache = false,
            shouldCancelIfNotInternet = true
        ){
            override suspend fun handleAPISuccessResponse(response: ApiSuccessResponse<GenericResponse>) {
                updateLocalDb(null)

                withContext(Main){
                    onCompleteJob(
                        DataState.data(
                            data = null,
                            response = Response(response.body.response, ResponseType.Toast)
                        )
                    )
                }
            }

            override fun createCall(): LiveData<GenericApiResponse<GenericResponse>> {
                return openApiMainService.saveAccountProperties(
                    "Token ${authToken.token}",
                    accountProperties.email,
                    accountProperties.username
                )
            }

            override fun setJob(job: Job) {
                addJob("saveAccountProperties", job)
            }

            // not used
            override fun loadFromCache(): LiveData<AccountViewState> {
                return AbsentLiveData.create()
            }

            override suspend fun updateLocalDb(cachedObject: Any?) {
                return accountPropertiesDAO.updateAccountProperties(
                    accountProperties.pk,
                    accountProperties.email,
                    accountProperties.username
                )
            }

            // not used
            override suspend fun createCacheRequestAndReturn() {

            }

        }.asLiveData()
    }

    fun updatePassword(
        authToken: AuthToken,
        currentPassword : String,
        newPassword : String,
        confirmNewPassword : String
    ): LiveData<DataState<AccountViewState>> {
        return object : NetworkBoundResource<GenericResponse, Any, AccountViewState>(
            sessionManager.isConnectedToInternet(),
            true,
            false,
            true
        ){
            override suspend fun handleAPISuccessResponse(response: ApiSuccessResponse<GenericResponse>) {

                withContext(Main){
                    onCompleteJob(
                        DataState.data(
                            data = null,
                            response = Response(response.body.response, ResponseType.Toast)
                        )
                    )
                }

            }

            override fun createCall(): LiveData<GenericApiResponse<GenericResponse>> {
                return openApiMainService.updatePassword(
                    "Token ${authToken.token}",
                    currentPassword,
                    newPassword,
                    confirmNewPassword
                )
            }

            override fun setJob(job: Job) {
                addJob("updatePassword", job)
            }

            override fun loadFromCache(): LiveData<AccountViewState> {
                return AbsentLiveData.create()
            }

            override suspend fun updateLocalDb(cachedObject: Any?) {

            }

            override suspend fun createCacheRequestAndReturn() {

            }

        }.asLiveData()
    }



}