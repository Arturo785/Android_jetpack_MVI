package com.ar.jetpackarchitecture.ui

import com.ar.jetpackarchitecture.util.Response
import com.ar.jetpackarchitecture.util.StateMessageCallback

interface UICommunicationListener {

    fun onResponseReceived(
        response: Response,
        stateMessageCallback: StateMessageCallback
    )

    fun displayProgressBar(isLoading: Boolean)

    fun expandAppBar()

    fun hideSoftKeyboard()

    fun isStoragePermissionGranted(): Boolean
}