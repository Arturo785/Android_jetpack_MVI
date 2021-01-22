package com.ar.jetpackarchitecture.ui.auth

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.ar.jetpackarchitecture.R
import com.ar.jetpackarchitecture.di.auth.AuthScope
import com.ar.jetpackarchitecture.models.AuthToken
import com.ar.jetpackarchitecture.ui.auth.state.AuthStateEvent
import com.ar.jetpackarchitecture.ui.auth.state.LoginFields
import kotlinx.android.synthetic.main.fragment_login.*
import javax.inject.Inject

@AuthScope
class LoginFragment
@Inject constructor(
    private val viewModelFactory: ViewModelProvider.Factory
) : Fragment(R.layout.fragment_login,){

    val viewModel : AuthViewModel by viewModels {
        viewModelFactory
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.cancelActiveJobs()
    }

    // they take the onSuper from BaseAuthFragment because of the inheritance
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        login_button.setOnClickListener {
            login()
        }

        subscribeObservers()
    }

    fun subscribeObservers(){
        viewModel.viewState.observe(viewLifecycleOwner, Observer {
            // to when they are saved data from the viewModel to set it to the inputs
            it.loginFields?.let { loginFields ->
                loginFields.login_email?.let { input_email.setText(it) }
                loginFields.login_password?.let { input_password.setText(it) }
            }
        })
    }

    // saves the data on the liveDataObject when we leave the fragment
    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.setLoginFields(
            LoginFields(
                input_email.text.toString(),
                input_password.text.toString()
            )
        )
    }

    private fun login(){
        viewModel.setStateEvent(AuthStateEvent.LoginAttemptEvent(
            input_email.text.toString(),
            input_password.text.toString()
        ))
    }

}