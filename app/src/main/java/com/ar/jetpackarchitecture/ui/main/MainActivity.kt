package com.ar.jetpackarchitecture.ui.main

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.ar.jetpackarchitecture.R
import com.ar.jetpackarchitecture.ui.BaseActivity
import com.ar.jetpackarchitecture.ui.auth.AuthActivity
import com.ar.jetpackarchitecture.ui.auth.AuthViewModel
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        subscribeObservers()
    }


    private fun subscribeObservers(){
        // gets it from the inheritance
        // we use this because it's the activity that holds all the fragments of the section
        sessionManager.cachedToken.observe(this, Observer { authToken ->
            if(authToken == null || authToken.account_pk == -1 || authToken.token == null){
                //Not logged in
                navAuthActivity()
            }
        })
    }

    private fun navAuthActivity(){
        val intent = Intent(this, AuthActivity::class.java)
        startActivity(intent)
        finish() // closes the activity
    }

}