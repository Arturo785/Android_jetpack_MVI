package com.ar.jetpackarchitecture.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.ar.jetpackarchitecture.api.auth.network_responses.LoginResponse
import com.ar.jetpackarchitecture.api.auth.network_responses.RegistrationResponse
import com.ar.jetpackarchitecture.models.AuthToken
import com.ar.jetpackarchitecture.repository.auth.AuthRepository
import com.ar.jetpackarchitecture.ui.BaseViewModel
import com.ar.jetpackarchitecture.ui.DataState
import com.ar.jetpackarchitecture.ui.auth.state.AuthStateEvent
import com.ar.jetpackarchitecture.ui.auth.state.AuthViewState
import com.ar.jetpackarchitecture.ui.auth.state.LoginFields
import com.ar.jetpackarchitecture.ui.auth.state.RegistrationFields
import com.ar.jetpackarchitecture.util.AbsentLiveData
import com.ar.jetpackarchitecture.util.GenericApiResponse
import kotlinx.coroutines.InternalCoroutinesApi
import javax.inject.Inject

class AuthViewModel @Inject constructor(
    val authRepository: AuthRepository
) : BaseViewModel<AuthStateEvent, AuthViewState>()
{



    override fun handleStateEvent(stateEvent: AuthStateEvent): LiveData<DataState<AuthViewState>> {
        when(stateEvent){
            // because of the sealed class we can receive custom responses from each case
            is AuthStateEvent.LoginAttemptEvent -> {
                return authRepository.attemptLogin(stateEvent.email, stateEvent.password)
            }
            is AuthStateEvent.RegisterAttemptEvent -> {
                return authRepository.attemptRegistration(
                    stateEvent.email,
                    stateEvent.username,
                    stateEvent.password,
                    stateEvent.password2
                )
            }
            is AuthStateEvent.CheckPreviousAuthEvent -> {
                return authRepository.checkPreviousAuthUser()
            }
        }
    }

    fun setRegistrationFields(registrationFields: RegistrationFields){
        // gets the old ones or new
        val update = getCurrentViewStateOrNew()

        if(update.registrationFields == registrationFields){
            return
            //nothing has change
        }
        update.registrationFields = registrationFields

        // the one from the baseViewModel
        _viewState.value = update
    }

    fun setLoginFields(loginFields: LoginFields){
        // gets the old ones or new
        val update = getCurrentViewStateOrNew()

        if(update.loginFields == loginFields){
            return
            //nothing has change
        }
        update.loginFields = loginFields

        // the one from the baseViewModel
        _viewState.value = update
    }

    fun setTokenFields(authToken: AuthToken){
        // gets the old ones or new
        val update = getCurrentViewStateOrNew()

        if(update.authToken == authToken){
            return
            //nothing has change
        }
        update.authToken = authToken

        // the one from the baseViewModel
        _viewState.value = update
    }

    override fun initNewViewState(): AuthViewState {
        return AuthViewState()
    }

    fun cancelActiveJobs(){
        authRepository.cancelActiveJobs()
    }

    // when the viewModel gets cleared
    override fun onCleared() {
        super.onCleared()
        cancelActiveJobs()
    }

}