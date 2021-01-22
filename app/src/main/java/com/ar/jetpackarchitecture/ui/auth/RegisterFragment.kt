package com.ar.jetpackarchitecture.ui.auth

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.ar.jetpackarchitecture.R
import com.ar.jetpackarchitecture.di.auth.AuthScope
import com.ar.jetpackarchitecture.ui.auth.state.AuthStateEvent
import com.ar.jetpackarchitecture.ui.auth.state.RegistrationFields
import kotlinx.android.synthetic.main.fragment_register.*
import javax.inject.Inject


@AuthScope
class RegisterFragment
@Inject constructor(
    private val viewModelFactory: ViewModelProvider.Factory
) : Fragment(R.layout.fragment_register,){

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


        register_button.setOnClickListener {
            register()
        }
        subscribeObservers()
    }

    fun subscribeObservers(){
        viewModel.viewState.observe(viewLifecycleOwner, Observer {
            // to when they are saved data from the viewModel to set it to the inputs
            it.registrationFields?.let { registrationFields ->
                registrationFields.registration_email?.let { input_email.setText(it) }
                registrationFields.registration_username?.let { input_username.setText(it) }
                registrationFields.registration_password?.let { input_password.setText(it) }
                registrationFields.registration_password_2?.let { input_password_confirm.setText(it) }
            }
        })
    }

    // saves the data on the liveDataObject when we leave the fragment
    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.setRegistrationFields(
            RegistrationFields(
                input_email.text.toString(),
                input_username.text.toString(),
                input_password.text.toString(),
                input_password_confirm.text.toString()
            )
        )
    }

    private fun register(){
        viewModel.setStateEvent(
            AuthStateEvent.RegisterAttemptEvent(
                input_email.text.toString(),
                input_username.text.toString(),
                input_password.text.toString(),
                input_password_confirm.text.toString()
            )
        )
    }

}