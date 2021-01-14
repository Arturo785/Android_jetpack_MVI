package com.ar.jetpackarchitecture.ui.main.account

import androidx.lifecycle.LiveData
import com.ar.jetpackarchitecture.models.AccountProperties
import com.ar.jetpackarchitecture.repository.main.AccountRepository
import com.ar.jetpackarchitecture.session.SessionManager
import com.ar.jetpackarchitecture.ui.BaseViewModel
import com.ar.jetpackarchitecture.ui.DataState
import com.ar.jetpackarchitecture.ui.Loading
import com.ar.jetpackarchitecture.ui.auth.state.AuthStateEvent
import com.ar.jetpackarchitecture.ui.main.account.state.AccountStateEvent
import com.ar.jetpackarchitecture.ui.main.account.state.AccountViewState
import com.ar.jetpackarchitecture.ui.main.blog.state.BlogViewState
import com.ar.jetpackarchitecture.util.AbsentLiveData
import javax.inject.Inject

class AccountViewModel @Inject constructor(
    val sessionManager: SessionManager,
    val accountRepository: AccountRepository
) : BaseViewModel<AccountStateEvent, AccountViewState>(){


    override fun handleStateEvent(stateEvent: AccountStateEvent): LiveData<DataState<AccountViewState>> {
        when(stateEvent){

            is AccountStateEvent.GetAccountPropertiesEvent ->{
                return sessionManager.cachedToken.value?.let {authToken ->
                    accountRepository.getAccountProperties(authToken)
                } ?: AbsentLiveData.create()
            }

            is AccountStateEvent.UpdateAccountPropertiesEvent -> {
                return sessionManager.cachedToken.value?.let {authToken ->
                    authToken.account_pk?.let { pk ->
                        accountRepository.saveAccountProperties(
                            authToken,
                            AccountProperties(
                                pk,
                                stateEvent.email,
                                stateEvent.username
                            )
                        )
                    }
                } ?: AbsentLiveData.create()
            }

            is AccountStateEvent.ChangePasswordEvent -> {
                return sessionManager.cachedToken.value?.let {authToken ->
                    accountRepository.updatePassword(
                        authToken,
                        stateEvent.currentPassword,
                        stateEvent.newPassword,
                        stateEvent.confirmNewPassword
                    )
                } ?: AbsentLiveData.create()
            }

            is AccountStateEvent.None -> {
                return object: LiveData<DataState<AccountViewState>>(){
                    override fun onActive() {
                        super.onActive()
                        value = DataState(null, Loading(false), null)
                    }
                }
            }
        }
    }

    override fun initNewViewState(): AccountViewState {
        return AccountViewState()
    }

    fun setAccountPropertiesData(accountProperties: AccountProperties){
        val update = getCurrentViewStateOrNew()

        if(update.accountProperties == accountProperties){
            return // no need to update
        }

        update.accountProperties = accountProperties

        _viewState.value = update
    }

    fun logout(){
        sessionManager.logout()
    }

    fun cancelActiveJobs(){
        handlePendingData()
        accountRepository.cancelActiveJobs()
    }

    fun handlePendingData(){
        setStateEvent(AccountStateEvent.None)
    }

    // when the viewModel gets cleared
    override fun onCleared() {
        super.onCleared()
        cancelActiveJobs()
    }



}