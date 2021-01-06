package com.ar.jetpackarchitecture.repository

import android.util.Log
import kotlinx.coroutines.Job

open class JobManager (
    private val className : String
){
    private val TAG = "JobManager"

    private val jobs : HashMap<String, Job> = HashMap()


    // if the user changes opinion, corrects something and sends the job again
    fun addJob(methodName : String, job: Job){
        cancelJob(methodName)
        jobs[methodName] = job
    }

    fun cancelJob(methodName: String) {
        getJob(methodName)?.cancel()
    }

    fun getJob(methodName: String): Job? {
        //Returns the value corresponding to the given key, or null if such a key is not present in the map.
        return jobs[methodName]
    }

    fun cancelActiveJobs(){
        for((methodJob, job) in jobs){
            if(job.isActive){
                Log.e(TAG, "cancelActiveJobs: $job")
                job.cancel()
            }
        }
    }


}