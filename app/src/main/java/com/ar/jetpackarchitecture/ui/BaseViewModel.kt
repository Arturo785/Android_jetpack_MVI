package com.ar.jetpackarchitecture.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel

// just as a reminder an abstract class is a helper class that has similar methods that can have impl
// and values but is not supposed to be used as an instance by itself but to be inherited

abstract class BaseViewModel<StateEvent, ViewState> : ViewModel(){

    val TAG : String = "BASE_VIEW_MODEL"

    protected val _stateEvent : MutableLiveData<StateEvent> = MutableLiveData()
    protected val _viewState : MutableLiveData<ViewState> = MutableLiveData()

    // basically creates a property and sets a custom getter in this case from _viewState
    // could be a fun that just returns _viewState, it is the same but shorter
    val viewState : LiveData<ViewState>
        get() = _viewState

    val dataState : LiveData<DataState<ViewState>> = Transformations
        .switchMap(_stateEvent){ stateEvent ->
            stateEvent?.let {
                handleStateEvent(stateEvent)
            }
        }

    // our data state handles what we receive
    // because each viewModel will have it's own stateEvent and ViewState
    // this has to be custom on each viewModel that's why it is abstract
    abstract fun handleStateEvent(stateEvent: StateEvent) : LiveData<DataState<ViewState>>

    abstract fun initNewViewState() : ViewState

    fun setStateEvent(event: StateEvent){
        _stateEvent.value = event
    }

    fun getCurrentViewStateOrNew() : ViewState{
        val value = viewState.value?.let {
            it
        } ?: initNewViewState()

        return value
    }



}