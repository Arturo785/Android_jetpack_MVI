package com.ar.jetpackarchitecture.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.ar.jetpackarchitecture.models.AuthToken
import com.ar.jetpackarchitecture.ui.DataState
import com.ar.jetpackarchitecture.ui.Response
import com.ar.jetpackarchitecture.ui.ResponseType
import com.ar.jetpackarchitecture.ui.auth.state.AuthViewState
import com.ar.jetpackarchitecture.util.*
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main

// this class is made to handle every db,cache,network resource the app makes


abstract class NetworkBoundResource<ResponseObject, CacheObject, ViewStateType> (
    isNetworkAvailable : Boolean,
    isNetworkRequest : Boolean,
    shouldLoadFromCache : Boolean,
    shouldCancelIfNotInternet : Boolean
)

{
    val TAG = "NetworkBoundResource"

    protected val result = MediatorLiveData<DataState<ViewStateType>>() // receives different sources
    protected lateinit var job : CompletableJob
    protected lateinit var coroutineScope : CoroutineScope

    init {
        setJob(initNewJob())
        setValue(DataState.loading(isLoading = true, cachedData = null))

        if(shouldLoadFromCache){
            val dbSource = loadFromCache()

            result.addSource(dbSource){
                result.removeSource(dbSource) // no need to have it we just process the data
                setValue(DataState.loading(true,cachedData = it))
            }
        }

        if(isNetworkRequest){

            if(isNetworkAvailable){
                doNetworkRequest()
            }

            else{
                if(shouldCancelIfNotInternet){
                    onErrorReturn(UNABLE_TODO_OPERATION_WO_INTERNET, shouldUseDialog = true, shouldUseToast = false)
                }
                else{
                    doCacheRequest()
                }

            }
        }

        // when no internet is needed
        else{
            doCacheRequest()
        }

    }

    private fun doCacheRequest() {
        coroutineScope.launch {
            // view data from cache and return
            createCacheRequestAndReturn()
        }
    }

    private fun doNetworkRequest(){
        // both coroutines have their own cycle
        coroutineScope.launch {

            withContext(Main){

                val apiResponse = createCall()
                result.addSource(apiResponse){response ->
                    result.removeSource(apiResponse) // no need to have it we just process the data

                    coroutineScope.launch {
                        handleNetworkCall(response)
                    }

                }
            }
        }

        GlobalScope.launch(IO) {
            // if this gets done means that the job has taken too long and should be cancelled
            delay(Constants.NETWORK_TIMEOUT)
            if(!job.isCompleted){
                Log.d(TAG, "Job timeout...: ")
                job.cancel(CancellationException(UNABLE_TO_RESOLVE_HOST))
            }
        }
    }

    private suspend fun handleNetworkCall(response: GenericApiResponse<ResponseObject>?) {

        when(response){

            is ApiSuccessResponse -> {
                handleAPISuccessResponse(response)
            }

            is ApiErrorResponse -> {
                onErrorReturn(response.errorMessage, true, false)
            }

            is ApiEmptyResponse -> {
                onErrorReturn(ERROR_UNKNOWN, true, false)
            }

        }

    }

    fun onErrorReturn(errorMessage : String?, shouldUseDialog : Boolean, shouldUseToast : Boolean){
        var msg = errorMessage
        var useDialog = shouldUseDialog
        var responseType : ResponseType = ResponseType.None

        if(msg == null){
            msg = ERROR_UNKNOWN
        }
        else if(isNetworkError(msg)){
            msg = ERROR_CHECK_NETWORK_CONNECTION
            useDialog = false
        }
        if(shouldUseToast){
            responseType = ResponseType.Toast
        }
        if(useDialog){
            responseType = ResponseType.Dialog
        }

        onCompleteJob(DataState.error(
            response = Response(
                message = msg,
                responseType = responseType
            )
        ))
    }

    fun onCompleteJob(dataState: DataState<ViewStateType>){
        GlobalScope.launch(Main) {
            // this will trigger job.invokeOnCompletion
            job.complete()
            setValue(dataState)
        }
    }

    private fun setValue(dataState: DataState<ViewStateType>) {
        result.value = dataState
    }

    @OptIn(InternalCoroutinesApi::class)
    private fun initNewJob() : Job{
        Log.d(TAG, "initNewJob: called...")
        job = Job()

        // when cancelled or completed
        job.invokeOnCompletion(onCancelling = true, invokeImmediately = true, handler = object : CompletionHandler{

            override fun invoke(cause: Throwable?) {
                if(job.isCancelled){
                    Log.d(TAG, "job: cancelled...")
                    cause?.let {
                        // error
                        onErrorReturn(it.message, false, true)
                    } ?: onErrorReturn(ERROR_UNKNOWN, false, true)
                }
                else if(job.isCompleted){
                    Log.d(TAG, "job completed...")
                    // OK
                }

            }
        })

        // creates a new coroutine in the IO thread but individually for the job for further operations
        coroutineScope = CoroutineScope(IO + job)
        return job

        //coroutineScope.cancel() cancels all jobs in the scope
    }

    fun asLiveData() = result as LiveData<DataState<ViewStateType>>

    /// ABSTRACT SECTION

    abstract suspend fun handleAPISuccessResponse(response : ApiSuccessResponse<ResponseObject>)

    abstract fun createCall() : LiveData<GenericApiResponse<ResponseObject>>

    abstract fun setJob(job: Job)

    abstract fun loadFromCache() : LiveData<ViewStateType>

    abstract suspend fun updateLocalDb(cachedObject : CacheObject?)

    abstract suspend fun createCacheRequestAndReturn()




}