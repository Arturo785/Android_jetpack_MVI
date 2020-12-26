package com.ar.jetpackarchitecture.ui.auth

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.ar.jetpackarchitecture.R
import com.ar.jetpackarchitecture.ui.BaseActivity
import com.ar.jetpackarchitecture.ui.main.MainActivity
import com.ar.jetpackarchitecture.viewmodels.ViewModelProviderFactory
import kotlinx.android.synthetic.main.fragment_login.*
import javax.inject.Inject

class AuthActivity : BaseActivity() {

    @Inject
    lateinit var providerFactory: ViewModelProviderFactory

    lateinit var viewModel : AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        viewModel = ViewModelProvider(this, providerFactory).get(AuthViewModel::class.java)
        subscribeObservers()
    }

    private fun subscribeObservers(){

        viewModel.viewState.observe(this, Observer {
            it.authToken?.let {
                sessionManager.login(it)
            }
        })

        // gets it from the inheritance
        // we use this because it's the activity that holds all the fragments of the section
        sessionManager.cachedToken.observe(this, Observer { authToken ->
            if(authToken != null && authToken.account_pk != -1 && authToken.token != null){
                //Not logged in
                navMainActivity()
            }
        })
    }

    private fun navMainActivity(){
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish() // closes the activity
    }
}