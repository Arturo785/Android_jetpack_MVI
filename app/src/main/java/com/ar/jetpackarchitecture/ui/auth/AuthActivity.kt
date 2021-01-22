package com.ar.jetpackarchitecture.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.fragment.app.FragmentFactory
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import com.ar.jetpackarchitecture.BaseApplication
import com.ar.jetpackarchitecture.R
import com.ar.jetpackarchitecture.fragments.auth.AuthFragmentFactory
import com.ar.jetpackarchitecture.fragments.auth.AuthNavHostFragment
import com.ar.jetpackarchitecture.ui.BaseActivity
import com.ar.jetpackarchitecture.ui.auth.state.AuthStateEvent
import com.ar.jetpackarchitecture.ui.main.MainActivity
import com.ar.jetpackarchitecture.viewmodels.AuthViewModelFactory
import kotlinx.android.synthetic.main.activity_auth.*
import javax.inject.Inject

class AuthActivity : BaseActivity() {

    @Inject
    lateinit var providerFactory: ViewModelProvider.Factory

    @Inject
    lateinit var fragmentFactory : FragmentFactory

    val viewModel : AuthViewModel by viewModels{
        providerFactory
    }


    override fun inject() {
        (application as BaseApplication).authComponent()
            .inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        subscribeObservers()
        onRestoreInstanceState()

    }

    fun onRestoreInstanceState(){
        val host = supportFragmentManager.findFragmentById(R.id.auth_fragments_container)
        host?.let {
            // do nothing
        } ?: createNavHost()
    }

    private fun createNavHost(){
        val navHost = AuthNavHostFragment.create(
            R.navigation.auth_nav_graph
        )
        supportFragmentManager.beginTransaction()
            .replace(
                R.id.auth_fragments_container,
                navHost,
                getString(R.string.AuthNavHost)
            )
            .setPrimaryNavigationFragment(navHost)
            .commit()
    }


    override fun onResume() {
        super.onResume()
        // triggers the observer in the viewModel on the handleStateEvent
        checkPreviousAuthUser()
    }

    private fun subscribeObservers(){

        viewModel.dataState.observe(this, Observer {dataState ->
            // checks any errors or messages to be shown
            onDataStateChange(dataState)
            // a chain of verification
            dataState.data?.let { data ->
                data.data?.let {event ->
                    event.getContentIfNotHandled()?.let {
                        it.authToken?.let {
                            // if token exists
                            viewModel.setTokenFields(it)
                        }
                    }
                }

            }

        })

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

    fun checkPreviousAuthUser(){
        viewModel.setStateEvent(AuthStateEvent.CheckPreviousAuthEvent)
    }

    private fun navMainActivity(){
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish() // closes the activity
        (application as BaseApplication).releaseAuthComponent()
    }


    override fun displayProgressBar(boolean: Boolean) {
        if(boolean){
            progress_bar.visibility = View.VISIBLE
        }
        else{
            progress_bar.visibility = View.INVISIBLE
        }
    }

    override fun expandAppBar() {
        // ignore
    }
}