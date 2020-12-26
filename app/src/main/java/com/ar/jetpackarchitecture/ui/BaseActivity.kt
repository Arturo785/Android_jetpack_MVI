package com.ar.jetpackarchitecture.ui

import com.ar.jetpackarchitecture.session.SessionManager
import dagger.android.support.DaggerAppCompatActivity
import javax.inject.Inject

abstract class BaseActivity : DaggerAppCompatActivity(){

    val TAG = "BaseActivity"

    @Inject
    lateinit var sessionManager: SessionManager
    
}