package com.ar.jetpackarchitecture.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import com.ar.jetpackarchitecture.BaseApplication
import com.ar.jetpackarchitecture.R
import com.ar.jetpackarchitecture.models.AUTH_TOKEN_KEY
import com.ar.jetpackarchitecture.models.AuthToken
import com.ar.jetpackarchitecture.ui.BaseActivity
import com.ar.jetpackarchitecture.ui.auth.AuthActivity
import com.ar.jetpackarchitecture.ui.main.account.BaseAccountFragment
import com.ar.jetpackarchitecture.ui.main.account.ChangePasswordFragment
import com.ar.jetpackarchitecture.ui.main.account.UpdateAccountFragment
import com.ar.jetpackarchitecture.ui.main.blog.BaseBlogFragment
import com.ar.jetpackarchitecture.ui.main.blog.UpdateBlogFragment
import com.ar.jetpackarchitecture.ui.main.blog.ViewBlogFragment
import com.ar.jetpackarchitecture.ui.main.create_blog.BaseCreateBlogFragment
import com.ar.jetpackarchitecture.util.BOTTOM_NAV_BACKSTACK_KEY
import com.ar.jetpackarchitecture.util.BottomNavController
import com.ar.jetpackarchitecture.util.setUpNavigation
import com.ar.jetpackarchitecture.viewmodels.AuthViewModelFactory
import com.bumptech.glide.RequestManager
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject
import javax.inject.Named

class MainActivity : BaseActivity(),
        BottomNavController.OnNavigationGraphChanged,
        BottomNavController.OnNavigationReselectedListener
{

    @Inject
    @Named("AccountFragmentFactory")
    lateinit var accountFragmentFactory : FragmentFactory

    @Inject
    @Named("BlogFragmentFactory")
    lateinit var blogFragmentFactory : FragmentFactory

    @Inject
    @Named("CreateBlogFragmentFactory")
    lateinit var createBlogFragmentFactory : FragmentFactory



    private lateinit var bottomNavigationView: BottomNavigationView

    private val bottomNavController by lazy(LazyThreadSafetyMode.NONE){
        BottomNavController(
            this,
            R.id.main_nav_host_fragment, // our host
            R.id.nav_blog, // initial fragment when launching
            this,
        )
    }

    override fun inject() {
        (application as BaseApplication).mainComponent().inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupActionBar()
        setupBottomNavigationView(savedInstanceState)

        subscribeObservers()
        restoreSession(savedInstanceState)
    }

    private fun setupBottomNavigationView(savedInstanceState: Bundle?){
        bottomNavigationView = bottom_navigation_view

        bottomNavigationView.setUpNavigation(bottomNavController, this) // the extension we made in
        // bottomNavController

        if(savedInstanceState == null){
            // first time in the app
            bottomNavController.setupBottomNavigationBackStack(null)
            bottomNavController.onNavigationItemSelected()
        }
        else{
            (savedInstanceState[BOTTOM_NAV_BACKSTACK_KEY] as IntArray?)?.let { items ->
                val backstack = BottomNavController.BackStack()
                backstack.addAll(items.toTypedArray())
                bottomNavController.setupBottomNavigationBackStack(backstack)
            }
        }
    }

    private fun restoreSession(savedInstanceState: Bundle?){
        savedInstanceState?.get(AUTH_TOKEN_KEY)?.let{ authToken ->
            sessionManager.setValue(authToken as AuthToken)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putParcelable(AUTH_TOKEN_KEY, sessionManager.cachedToken.value)
        outState.putIntArray(BOTTOM_NAV_BACKSTACK_KEY, bottomNavController.navigationBackStack.toIntArray())
        super.onSaveInstanceState(outState)
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
        (application as BaseApplication).releaseMainComponent()
    }

    override fun displayProgressBar(boolean: Boolean) {
        if(boolean){
            progress_bar.visibility = View.VISIBLE
        }
        else{
            progress_bar.visibility = View.INVISIBLE
        }
    }


    override fun onGraphChange() {
        expandAppBar() // because of a bug that hides the appBar
        cancelActiveJobs()
    }

    private fun cancelActiveJobs(){
        val fragments = bottomNavController.fragmentManager
            .findFragmentById(bottomNavController.containerId)
            ?.childFragmentManager
            ?.fragments
        if(fragments != null){
            for(fragment in fragments){

                when(fragment){
                    is BaseAccountFragment -> fragment.cancelActiveJobs()
                    is BaseBlogFragment -> fragment.cancelActiveJobs()
                    is BaseCreateBlogFragment -> fragment.cancelActiveJobs()
                }
            }
        }
        displayProgressBar(false)
    }

    // to manage tap on the bottomNav when in the same host but not homeFragment
    override fun onReselectNavItem(navController: NavController, fragment: Fragment) = when(fragment){
        is ViewBlogFragment -> {
            navController.navigate(R.id.action_viewBlogFragment_to_blogFragment)
        }

        is UpdateBlogFragment -> {
            navController.navigate(R.id.action_updateBlogFragment_to_blogFragment)
        }

        is UpdateAccountFragment -> {
            navController.navigate(R.id.action_updateAccountFragment_to_accountFragment)
        }

        is ChangePasswordFragment -> {
            navController.navigate(R.id.action_changePasswordFragment_to_accountFragment)
        }
        else -> {
            // do nothing
        }
    }

    override fun onBackPressed() = bottomNavController.onBackPressed()

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home -> onBackPressed()
        }

        return super.onOptionsItemSelected(item)

    }

    private fun setupActionBar(){
        setSupportActionBar(tool_bar)
    }

    override fun expandAppBar() {
        findViewById<AppBarLayout>(R.id.app_bar).setExpanded(true)
    }



}