package com.ar.jetpackarchitecture.ui

data class DataState<T>(
    var error : Event<StateError>? = null,
    var loading : Loading = Loading(false),
    var data : Data<T>? = null
){

    //like our builders for every case
    companion object{
        // every one gets filled with only the necessary to each case
        fun <T> error(response: Response): DataState<T> {
            return DataState(error = Event(StateError(response)))
        }

        // this one is kind of tricky with the data response but uses the constructors from the Event class
        // from the companion object
        fun <T> loading (isLoading: Boolean, cachedData : T? = null) : DataState<T>{
            return DataState(loading = Loading(isLoading), data = Data(Event.dataEvent(cachedData), null))
        }

        fun <T> data(data : T? = null, response : Response? = null) : DataState<T>{
            return DataState(
                data = Data(
                    Event.dataEvent(data),
                    Event.responseEvent(response)
                )
            )
        }
    }
}