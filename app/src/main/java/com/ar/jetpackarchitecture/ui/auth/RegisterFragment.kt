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


class RegisterFragment : BaseAuthFragment(){

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    // they take the onSuper from BaseAuthFragment because of the inheritance
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // inherits from the BaseAuthFragment therefore has access to TAG and ViewModel
        Log.d(TAG, "RegisterFragment: ${viewModel.hashCode()}: ")

        viewModel.testRegistrationRequest("joarceus2@hotmail.com", "justMyTest", "myPassword2", "myPassword2").observe(
            viewLifecycleOwner,
            Observer { response ->
                when (response) {
                    is ApiSuccessResponse -> {
                        Log.d(TAG, "Success register: ${response.body} ")
                    }
                    is ApiErrorResponse -> {
                        Log.d(TAG, "Error register: ${response.errorMessage} ")
                    }
                    is ApiEmptyResponse -> {
                        Log.d(TAG, "EMPTY register:")
                    }
                }
            }
        )
    }

}