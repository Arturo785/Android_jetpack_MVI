package com.ar.jetpackarchitecture.ui.main.blog

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.ar.jetpackarchitecture.R
import com.ar.jetpackarchitecture.ui.DataStateChangeListener

import dagger.android.support.DaggerFragment

abstract class BaseBlogFragment : DaggerFragment(){

    val TAG: String = "BaseBlogFragment"

    lateinit var stateChangeListener: DataStateChangeListener

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try{
            stateChangeListener = context as DataStateChangeListener
        }catch(e: ClassCastException){
            Log.e(TAG, "$context must implement DataStateChangeListener" )
        }
    }

    fun cancelActiveJobs(){
        //viewModel.cancelActiveJobs()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupActionBarWithNavController(R.id.blogFragment, activity as AppCompatActivity)

        cancelActiveJobs()
    }

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