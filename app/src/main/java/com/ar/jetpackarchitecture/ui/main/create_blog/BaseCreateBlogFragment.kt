package com.ar.jetpackarchitecture.ui.main.create_blog

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.ar.jetpackarchitecture.R
import com.ar.jetpackarchitecture.ui.DataStateChangeListener
import com.ar.jetpackarchitecture.ui.UICommunicationListener

abstract class BaseCreateBlogFragment constructor(
    @LayoutRes
    private val layoutRes: Int
): Fragment(layoutRes) {

    val TAG: String = "AppDebug"


    lateinit var stateChangeListener: DataStateChangeListener

    lateinit var uiCommunicationListener: UICommunicationListener

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try{
            stateChangeListener = context as DataStateChangeListener
        }catch(e: ClassCastException){
            Log.e(TAG, "$context must implement DataStateChangeListener" )
        }

        try{
            uiCommunicationListener = context as UICommunicationListener
        }catch(e: ClassCastException){
            Log.e(TAG, "$context must implement UICommunicationListener" )
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupActionBarWithNavController(R.id.createBlogFragment, activity as AppCompatActivity)
    }

/*    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        cancelActiveJobs()

        // Restore state after process death
        savedInstanceState?.let { inState ->
            (inState[CREATE_BLOG_VIEW_KEY] as CreateBlogViewState?)?.let { viewState ->
                viewModel.setViewState(viewState)
            }
        }
    }


    override fun onSaveInstanceState(outState: Bundle) {
        outState.putParcelable(
            CREATE_BLOG_VIEW_KEY,
            viewModel.viewState.value
        )

        super.onSaveInstanceState(outState)
    }*/

    abstract fun cancelActiveJobs()


    // deletes the backArrow on the fragments inside the setOf
    fun setupActionBarWithNavController(fragmentId : Int, activity : AppCompatActivity){
        val appBarConfiguration = AppBarConfiguration(setOf(fragmentId))

        NavigationUI.setupActionBarWithNavController(
            activity,
            findNavController(),
            appBarConfiguration
        )
    }
}