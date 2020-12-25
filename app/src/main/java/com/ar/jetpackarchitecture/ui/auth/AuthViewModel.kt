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
import javax.inject.Inject

class AuthViewModel @Inject constructor(
    val authRepository: AuthRepository
) : BaseViewModel<AuthStateEvent, AuthViewState>()
{



    override fun handleStateEvent(stateEvent: AuthStateEvent): LiveData<DataState<AuthViewState>> {
        when(stateEvent){
            is AuthStateEvent.LoginAttemptEvent -> {
                return AbsentLiveData.create()
            }
            is AuthStateEvent.RegisterAttemptEvent -> {
                return AbsentLiveData.create()
            }
            is AuthStateEvent.CheckPreviousAuthEvent -> {
                return AbsentLiveData.create()
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

}