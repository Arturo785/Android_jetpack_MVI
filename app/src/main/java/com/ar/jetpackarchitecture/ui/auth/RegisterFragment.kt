package com.ar.jetpackarchitecture.ui.auth

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.ar.jetpackarchitecture.R
import com.ar.jetpackarchitecture.ui.auth.state.LoginFields
import com.ar.jetpackarchitecture.ui.auth.state.RegistrationFields
import kotlinx.android.synthetic.main.fragment_register.*


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

}