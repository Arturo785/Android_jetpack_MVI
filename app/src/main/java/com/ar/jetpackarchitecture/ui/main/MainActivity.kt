package com.ar.jetpackarchitecture.ui.main

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import com.ar.jetpackarchitecture.R
import com.ar.jetpackarchitecture.ui.BaseActivity
import com.ar.jetpackarchitecture.ui.auth.AuthActivity
import com.ar.jetpackarchitecture.ui.main.account.ChangePasswordFragment
import com.ar.jetpackarchitecture.ui.main.account.UpdateAccountFragment
import com.ar.jetpackarchitecture.ui.main.blog.UpdateBlogFragment
import com.ar.jetpackarchitecture.ui.main.blog.ViewBlogFragment
import com.ar.jetpackarchitecture.util.BottomNavController
import com.ar.jetpackarchitecture.util.setUpNavigation
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity(),
        BottomNavController.NavGraphProvider,
        BottomNavController.OnNavigationGraphChanged,
        BottomNavController.OnNavigationReselectedListener
{

    private lateinit var bottomNavigationView: BottomNavigationView

    private val bottomNavController by lazy(LazyThreadSafetyMode.NONE){
        BottomNavController(
            this,
            R.id.main_nav_host_fragment, // our host
            R.id.nav_blog, // initial fragment when launching
            this,
            this // the interfaces
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupActionBar()
        bottomNavigationView = bottom_navigation_view

        bottomNavigationView.setUpNavigation(bottomNavController, this) // the extension we made in
        // bottomNavController

        if(savedInstanceState == null){
            // first time in the app
            bottomNavController.onNavigationItemSelected()
        }

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

    override fun displayProgressBar(boolean: Boolean) {
        if(boolean){
            progress_bar.visibility = View.VISIBLE
        }
        else{
            progress_bar.visibility = View.INVISIBLE
        }
    }



    //Methods from BottomNavController
    override fun getNavGraphId(itemId: Int) = when(itemId) {
        R.id.nav_blog -> {
            R.navigation.nav_blog
        }
        R.id.nav_account -> {
            R.navigation.nav_account
        }
        R.id.nav_create_blog -> {
            R.navigation.nav_create_blog
        }
        else -> {
            R.navigation.nav_blog
        }
    }

    override fun onGraphChange() {
        expandAppBar() // because of a bug that hides the appBar
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