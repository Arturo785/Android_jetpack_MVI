package com.ar.jetpackarchitecture.ui.auth

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.ar.jetpackarchitecture.R
import com.ar.jetpackarchitecture.util.ApiEmptyResponse
import com.ar.jetpackarchitecture.util.ApiErrorResponse
import com.ar.jetpackarchitecture.util.ApiSuccessResponse


class LoginFragment : BaseAuthFragment(){

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    // they take the onSuper from BaseAuthFragment because of the inheritance
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // inherits from the BaseAuthFragment therefore has access to TAG and ViewModel
        Log.d(TAG, "LoginFragment: ${viewModel.hashCode()}: ")

        viewModel.testLoginRequest("joarceus@hotmail.com", "pitonpastel").observe(
            viewLifecycleOwner,
            Observer { response ->
                when (response) {
                    is ApiSuccessResponse -> {
                        Log.d(TAG, "Success: ${response.body} ")
                    }
                    is ApiErrorResponse -> {
                        Log.d(TAG, "Error: ${response.errorMessage} ")
                    }
                    is ApiEmptyResponse -> {
                        Log.d(TAG, "EMPTY:")
                    }
                }
            }
        )
    }

}