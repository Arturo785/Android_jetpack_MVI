package com.ar.jetpackarchitecture.ui.auth

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.ar.jetpackarchitecture.R
import com.ar.jetpackarchitecture.models.AuthToken
import com.ar.jetpackarchitecture.ui.auth.state.LoginFields
import kotlinx.android.synthetic.main.fragment_login.*


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

        //Just for test
        login_button.setOnClickListener{
            viewModel.setTokenFields(
                AuthToken(1,"aasdsfffsfsf")
            )
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

}