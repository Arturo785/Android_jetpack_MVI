package com.ar.jetpackarchitecture.repository.main

import com.ar.jetpackarchitecture.di.main.MainScope
import com.ar.jetpackarchitecture.models.AuthToken
import com.ar.jetpackarchitecture.ui.main.account.state.AccountViewState
import com.ar.jetpackarchitecture.util.DataState
import com.ar.jetpackarchitecture.util.StateEvent
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow

@FlowPreview
@MainScope
interface AccountRepository {

    fun getAccountProperties(
        authToken: AuthToken,
        stateEvent: StateEvent
    ): Flow<DataState<AccountViewState>>

    fun saveAccountProperties(
        authToken: AuthToken,
        email: String,
        username: String,
        stateEvent: StateEvent
    ): Flow<DataState<AccountViewState>>

    fun updatePassword(
        authToken: AuthToken,
        currentPassword: String,
        newPassword: String,
        confirmNewPassword: String,
        stateEvent: StateEvent
    ): Flow<DataState<AccountViewState>>
}