package com.ar.jetpackarchitecture.util

import androidx.lifecycle.LiveData

class AbsentLiveData <T : Any?> private constructor() : LiveData<T>(){

    init {
        postValue(null)
    }

    // basically just creates a liveData object with null value
    companion object{
        fun <T> create() : LiveData<T>{
            return AbsentLiveData()
        }
    }
}