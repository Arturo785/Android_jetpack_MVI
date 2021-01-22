package com.ar.jetpackarchitecture.ui.auth

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.animation.TranslateAnimation
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.ar.jetpackarchitecture.R
import com.ar.jetpackarchitecture.ui.DataState
import com.ar.jetpackarchitecture.ui.DataStateChangeListener
import com.ar.jetpackarchitecture.ui.Response
import com.ar.jetpackarchitecture.ui.ResponseType
import com.ar.jetpackarchitecture.util.Constants
import kotlinx.android.synthetic.main.fragment_forgot_password.*
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject


class ForgotPasswordFragment
@Inject constructor(
    private val viewModelFactory: ViewModelProvider.Factory
) : Fragment(R.layout.fragment_forgot_password,){

    val viewModel : AuthViewModel by viewModels {
        viewModelFactory
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.cancelActiveJobs()
    }

    lateinit var webView : WebView

    lateinit var stateChangeListener : DataStateChangeListener // needed because it's a fragment and
    // not included on the activity like the BaseActivity

    val webInteractionCallBack : WebAppInterface.OnWebInteractionCallBack =
        object : WebAppInterface.OnWebInteractionCallBack{
            override fun onSuccess(email: String) {
                onPasswordResetLinkSent()
            }

            override fun onError(error: String) {
                val dataState = DataState.error<Any>(
                    response = Response(error, ResponseType.Dialog)
                )

                stateChangeListener.onDataStateChange(
                    dataState
                )
            }

            override fun onLoading(isLoading: Boolean) {
                // because it works on an IO thread while working on the webView
                GlobalScope.launch(Main){
                    stateChangeListener.onDataStateChange(
                        DataState.loading(isLoading= isLoading, cachedData = null)
                    )
                }
            }

        }

    private fun onPasswordResetLinkSent() {
        GlobalScope.launch(Main) {
            parent_view.removeView(webView)
            webView.destroy()

            val animation = TranslateAnimation(
                password_reset_done_container.width.toFloat(),
                0f,
                0f,
                0f
            )

            animation.duration = 500
            password_reset_done_container.startAnimation(animation)
            password_reset_done_container.visibility = View.VISIBLE
        }
    }


    // they take the onSuper from BaseAuthFragment because of the inheritance
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        webView = webview
        // inherits from the BaseAuthFragment therefore has access to TAG and ViewModel

        loadPasswordResetWebView()

        return_to_launcher_fragment.setOnClickListener {
            findNavController().popBackStack()
        }

    }

    @SuppressLint("SetJavaScriptEnabled")
    fun loadPasswordResetWebView(){
        stateChangeListener.onDataStateChange(
            DataState.loading(isLoading = true, cachedData = null)
        )

        webView.webViewClient = object : WebViewClient() {

            // when it is loaded
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                stateChangeListener.onDataStateChange(
                    DataState.loading(isLoading = false, cachedData = null)
                )
            }
        }
        webView.loadUrl(Constants.PASSWORD_RESET_URL)
        webView.settings.javaScriptEnabled = true
        webView.addJavascriptInterface(WebAppInterface(webInteractionCallBack), "AndroidTextListener") // name comes from server
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            stateChangeListener = context as DataStateChangeListener
        }
        catch (e : ClassCastException){

        }
    }

    class WebAppInterface constructor(
        private val callback : OnWebInteractionCallBack
    ){
        val TAG = "WebAppInterface"

        @JavascriptInterface
        fun onSuccess(email: String){
            callback.onSuccess(email)
        }

        @JavascriptInterface
        fun onError(error: String){
            callback.onError(error)
        }

        @JavascriptInterface
        fun onLoading(isLoading: Boolean){
            callback.onLoading(isLoading)
        }


        // all this comes from the server side
        interface OnWebInteractionCallBack{

            fun onSuccess(email : String)
            fun onError(error : String)
            fun onLoading(isLoading : Boolean)
        }
    }
}