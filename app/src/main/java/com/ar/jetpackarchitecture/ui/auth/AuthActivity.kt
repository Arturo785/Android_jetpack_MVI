package com.ar.jetpackarchitecture.ui.auth

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.ar.jetpackarchitecture.R
import com.ar.jetpackarchitecture.ui.BaseActivity

class AuthActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)
    }
}