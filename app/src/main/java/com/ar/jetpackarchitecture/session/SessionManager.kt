package com.ar.jetpackarchitecture.session

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ar.jetpackarchitecture.models.AuthToken
import com.ar.jetpackarchitecture.persistence.AuthTokenDAO
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.Exception

// used for dagger
@Singleton
class SessionManager @Inject constructor(
    val authTokenDAO: AuthTokenDAO,
    val application: Application
)
{
    private val TAG = "SessionManager"

    private val _cachedToken = MutableLiveData<AuthToken>()

    val cachedToken : LiveData<AuthToken>
        get() = _cachedToken

    fun login(newValue : AuthToken){
        setValue(newValue)
    }

    fun setValue(newValue : AuthToken?){
        //sets the value on the main thread otherwise fails
        GlobalScope.launch(Main) {
            if(_cachedToken.value != newValue){ // if is the same is no necessary to change it
                _cachedToken.value = newValue
            }
        }
    }

    fun logout(){
        GlobalScope.launch(IO) {
            var errorMessage : String? = null

            // remove from DB
            try {
                cachedToken.value?.account_pk?.let {
                    authTokenDAO.nullifyToken(it)
                }
            }
            catch (e: CancellationException){
                errorMessage = e.message
            }
            catch (e: Exception){
                errorMessage += "\n ${e.message}"
            }
            finally {
                errorMessage?.let {
                    Log.e(TAG, "logout: ${errorMessage}", )
                }
                setValue(null)
            }
        }

    }


    fun isConnectedToInternet(): Boolean {
        val result: Boolean

        val connectivityManager =
            application.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val networkCapabilities = connectivityManager.activeNetwork ?: return false
        val actNw =
            connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false

        result = when {
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }

        return result
    }

}