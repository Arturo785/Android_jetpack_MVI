package com.ar.jetpackarchitecture.ui.auth

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.ar.jetpackarchitecture.viewmodels.ViewModelProviderFactory
import dagger.android.support.DaggerFragment
import java.lang.Exception
import javax.inject.Inject

abstract class BaseAuthFragment : DaggerFragment(){

    val TAG = "BaseAuthFragment"

    @Inject
    lateinit var providerFactory: ViewModelProviderFactory

    lateinit var viewModel: AuthViewModel


    // with this they use the same ViewModel when they extend the BaseFragment
    //This on superCalls the "normal" fragment
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = activity?.run {
            ViewModelProvider(this, providerFactory).get(AuthViewModel::class.java)
        } ?: throw Exception("Invalid activity")

        // cancells all jobs when changing whole  fragment
        cancelActiveJobs()
    }

    fun cancelActiveJobs(){
        viewModel.cancelActiveJobs()
    }

}