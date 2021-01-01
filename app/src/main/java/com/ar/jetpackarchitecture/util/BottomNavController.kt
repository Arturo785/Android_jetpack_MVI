package com.ar.jetpackarchitecture.util

import android.app.Activity
import android.content.Context
import androidx.annotation.IdRes
import androidx.annotation.NavigationRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.ar.jetpackarchitecture.R
import com.google.android.material.bottomnavigation.BottomNavigationView


/**
 * Class credit: Allan Veloso
 * I took the concept from Allan Veloso and made alterations to fit our needs.
 * https://stackoverflow.com/questions/50577356/android-jetpack-navigation-bottomnavigationview-with-youtube-or-instagram-like#_=_
 * @property navigationBackStack: Backstack for the bottom navigation
 */

class BottomNavController(
    val context: Context,
    @IdRes val containerId : Int,
    @IdRes val appStartDestinationId : Int,
    val graphChangeListener: OnNavigationGraphChanged?, // in case no change is needed(for another project)
    val navGraphProvider: NavGraphProvider
) {

    val TAG = "BottomNavController"

    lateinit var activity : Activity
    lateinit var fragmentManager : FragmentManager
    lateinit var navItemChangeListener : OnNavigationItemChanged

    private val navigationBackStack = BackStack.of(appStartDestinationId)

    init {
        if(context is Activity){
            activity = context
            fragmentManager = (activity as FragmentActivity).supportFragmentManager
        }
    }

    // if nothing passed the lastFragment on the backStack is selected
    fun onNavigationItemSelected(itemId : Int = navigationBackStack.last()) : Boolean{

        // replace the fragment representing a navigation item (gets it)
        val fragment = fragmentManager.findFragmentByTag(itemId.toString())
            ?: NavHostFragment.create(navGraphProvider.getNavGraphId(itemId))

        fragmentManager.beginTransaction()
            .setCustomAnimations(
                R.anim.fade_in,
                R.anim.fade_out,
                R.anim.fade_in,
                R.anim.fade_out
            )
            .replace(containerId, fragment, itemId.toString())
            .addToBackStack(null)
            .commit()

        //add to backStack
        navigationBackStack.moveLast(itemId)

        // update checked item
        navItemChangeListener.onItemChanged(itemId)

        // communicate with activity
        graphChangeListener?.onGraphChange()

        return true
    }

    fun onBackPressed() {
        val childFragmentManager = fragmentManager.findFragmentById(containerId)!!
            .childFragmentManager
        when {
            // We should always try to go back on the child fragment manager stack before going to
            // the navigation stack. It's important to use the child fragment manager instead of the
            // NavController because if the user change tabs super fast commit of the
            // supportFragmentManager may mess up with the NavController child fragment manager back
            // stack

            childFragmentManager.popBackStackImmediate() -> {
                // this are the normal ones inside navControllers
            }

            //the bigOnes in navigation res folder
            // Fragment back stack is empty so try to go back on the navigation stack
            // navigationBackStack is the one that controls the bottom nav (the big ones in navigation res folder)
            navigationBackStack.size > 1 -> {
                // Remove last item from back stack
                navigationBackStack.removeLast()

                // Update the container with new fragment
                onNavigationItemSelected() // gets the last if nothing passed

            }
            // If the stack has only one and it's not the navigation home we should
            // ensure that the application always leave from startDestination
            // so we go to the appStartDestinationId
            navigationBackStack.last() != appStartDestinationId -> {
                navigationBackStack.removeLast()
                navigationBackStack.add(0, appStartDestinationId)
                onNavigationItemSelected()
            }
            // Navigation stack is empty, so finish the activity
            else -> activity.finish()
        }
    }




    // receives a fun as argument
    fun setOnItemNavigationChanged(listener : (itemId : Int) -> Unit){

        this.navItemChangeListener = object : OnNavigationItemChanged {

            override fun onItemChanged(item: Int) {
                listener.invoke(item)
                //listener(item) I think both are the same
            }

        }
    }

    //INTERFACES ---------------------------------------------------------

    // for setting the checked icon in the bottom nav
    interface OnNavigationItemChanged{
        fun onItemChanged(item : Int)
    }


    // get id's of every nav_graph
    interface NavGraphProvider{
        @NavigationRes
        fun getNavGraphId(itemId : Int) : Int
    }

    // for knowing when the graph changed
    interface OnNavigationGraphChanged{
        fun onGraphChange()
    }

    interface OnNavigationReselectedListener{
        fun onReselectNavItem(navController : NavController, fragment : Fragment)
    }


    //BACKSTACK MANAGER ---------------------------------------------------------
    private class BackStack : ArrayList<Int>(){

        companion object{

            fun of(vararg elements : Int): BackStack {
                val b = BackStack()
                b.addAll(elements.toTypedArray())
                return b
            }
        }

        fun removeLast() = removeAt(size -1)

        fun moveLast(item : Int){
            remove(item)
            add(item)
        }

    }

}

// EXTENSION FUN ---------------------------------------------------------
fun BottomNavigationView.setUpNavigation(
    bottomNavController: BottomNavController,
    onReselectListener : BottomNavController.OnNavigationReselectedListener
){
    setOnNavigationItemSelectedListener {
        bottomNavController.onNavigationItemSelected(it.itemId)
    }


    // all this needed for bug when re-selecting a fragment on bottomNav
    // maybe the approach from avoApp may work when on reeselecting
    // acceses the backStack for the fragments that are in their individual navHost
    // for each one of the navHosts
    // like a secondary backStack for the fragments in each navHost
    setOnNavigationItemReselectedListener {
        bottomNavController
            .fragmentManager.findFragmentById(bottomNavController.containerId)!!
            .childFragmentManager
            .fragments[0]?.let {fragment ->

                onReselectListener.onReselectNavItem(
                    bottomNavController.activity.findNavController(bottomNavController.containerId),
                    fragment
                )
        }

    }

    bottomNavController.setOnItemNavigationChanged {itemId ->
        menu.findItem(itemId).isChecked = true
    }
}