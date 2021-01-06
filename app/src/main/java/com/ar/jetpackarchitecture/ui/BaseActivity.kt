package com.ar.jetpackarchitecture.ui

import android.content.Context
import android.util.Log
import android.view.inputmethod.InputMethodManager
import com.ar.jetpackarchitecture.session.SessionManager
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

abstract class BaseActivity : DaggerAppCompatActivity(), DataStateChangeListener{

    val TAG = "BaseActivity"

    @Inject
    lateinit var sessionManager: SessionManager

    override fun onDataStateChange(dataState: DataState<*>?) {
        dataState?.let {
            GlobalScope.launch(Main) {
                displayProgressBar(it.loading.isLoading)
            }

            it.error?.let { errorEvent ->
                // and error exists
                handleStateError(errorEvent)
            }

            it.data?.let {
                it.response?.let {responseEvent ->
                    handleStateResponse(responseEvent)
                }
            }
        }
    }

    private fun handleStateResponse(responseEvent: Event<Response>) {
        responseEvent.getContentIfNotHandled()?.let {
            when(it.responseType){

                is ResponseType.Toast -> {
                    it.message?.let { message ->
                        displayToast(message) // comes from viewExtensions
                    }
                }

                is ResponseType.Dialog -> {
                    it.message?.let {message ->
                        displaySuccessDialog(message)
                    }
                }

                is ResponseType.None -> {
                    Log.d(TAG, "handleStateError: ${it.message}")
                }

            }
        }
    }

    private fun handleStateError(errorEvent: Event<StateError>) {
        errorEvent.getContentIfNotHandled()?.let {
            when(it.response.responseType){

                is ResponseType.Toast -> {
                    it.response.message?.let { message ->
                        displayToast(message) // comes from viewExtensions
                    }
                }

                is ResponseType.Dialog -> {
                    it.response.message?.let {message ->
                        displayErrorDialog(message)
                    }
                }

                is ResponseType.None -> {
                    Log.d(TAG, "handleStateError: ${it.response.message}")
                }

            }
        }
    }

    abstract fun displayProgressBar(boolean: Boolean)

    override fun hideSoftKeyboard() {
        if(currentFocus != null){
            val inputMethodManager = getSystemService(
                Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
        }
    }
}