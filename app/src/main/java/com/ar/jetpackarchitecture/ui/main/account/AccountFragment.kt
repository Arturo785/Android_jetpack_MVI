package com.ar.jetpackarchitecture.ui.main.account

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.ar.jetpackarchitecture.R
import com.ar.jetpackarchitecture.di.main.MainScope
import com.ar.jetpackarchitecture.models.AccountProperties
import com.ar.jetpackarchitecture.session.SessionManager
import com.ar.jetpackarchitecture.ui.main.account.state.ACCOUNT_VIEW_STATE_BUNDLE_KEY
import com.ar.jetpackarchitecture.ui.main.account.state.AccountStateEvent
import com.ar.jetpackarchitecture.ui.main.account.state.AccountViewState
import kotlinx.android.synthetic.main.fragment_account.*
import javax.inject.Inject

@MainScope
class AccountFragment @Inject constructor(
    private val viewModelFactory : ViewModelProvider.Factory
): BaseAccountFragment(R.layout.fragment_account){

    val viewModel : AccountViewModel by viewModels {
        viewModelFactory
    }

        override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // cancelling when changing fragment
        cancelActiveJobs()

        // Restore state after process death
        savedInstanceState?.let { inState ->
            (inState[ACCOUNT_VIEW_STATE_BUNDLE_KEY] as AccountViewState?)?.let { viewState ->
                viewModel.setViewState(viewState)
            }
        }
    }

        override fun onSaveInstanceState(outState: Bundle) {
        outState.putParcelable(
            ACCOUNT_VIEW_STATE_BUNDLE_KEY,
            viewModel.viewState.value
        )

        super.onSaveInstanceState(outState)
    }

    override fun cancelActiveJobs() {
        viewModel.cancelActiveJobs()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        change_password.setOnClickListener {
            findNavController().navigate(R.id.action_accountFragment_to_changePasswordFragment)
        }

        logout_button.setOnClickListener {
            viewModel.logout()
        }

        subscribeObservers()
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.edit_view_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId){

            R.id.edit -> {
                findNavController().navigate(R.id.action_accountFragment_to_updateAccountFragment)
                return true
            }

        }

        return super.onOptionsItemSelected(item)
    }


    private fun subscribeObservers(){
        viewModel.dataState.observe(viewLifecycleOwner, Observer{ dataState ->
            // checks any errors or messages to be shown
            if(dataState != null){
                stateChangeListener.onDataStateChange(dataState)
                dataState.data?.let { data ->
                    data.data?.let{ event ->
                        event.getContentIfNotHandled()?.let{ viewState ->
                            viewState.accountProperties?.let{ accountProperties ->
                                Log.d(TAG, "AccountFragment, DataState: ${accountProperties}")
                                viewModel.setAccountPropertiesData(accountProperties)
                            }
                        }
                    }
                }
            }
        })

        viewModel.viewState.observe(viewLifecycleOwner, Observer{ viewState->
            if(viewState != null){
                viewState.accountProperties?.let{
                    Log.d(TAG, "AccountFragment, ViewState: ${it}")
                    setAccountDataFields(it)
                }
            }
        })
    }

    // to reShow data when resuming
    override fun onResume() {
        super.onResume()
        viewModel.setStateEvent(
            AccountStateEvent.GetAccountPropertiesEvent
        )
    }

    private fun setAccountDataFields(accountProperties : AccountProperties){
        email?.text = accountProperties.email
        username?.text = accountProperties.username
    }
}