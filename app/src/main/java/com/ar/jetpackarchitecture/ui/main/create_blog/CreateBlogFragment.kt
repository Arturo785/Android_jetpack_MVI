package com.ar.jetpackarchitecture.ui.main.create_blog

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.*
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.ar.jetpackarchitecture.R
import com.ar.jetpackarchitecture.di.main.MainScope
import com.ar.jetpackarchitecture.ui.*
import com.ar.jetpackarchitecture.ui.main.create_blog.state.CREATE_BLOG_VIEW_KEY
import com.ar.jetpackarchitecture.ui.main.create_blog.state.CreateBlogStateEvent
import com.ar.jetpackarchitecture.ui.main.create_blog.state.CreateBlogViewState
import com.ar.jetpackarchitecture.util.Constants.Companion.GALLERY_REQUEST_CODE
import com.ar.jetpackarchitecture.util.ERROR_MUST_SELECT_IMAGE
import com.ar.jetpackarchitecture.util.ERROR_SOMETHING_WRONG_WITH_IMAGE
import com.ar.jetpackarchitecture.util.SuccessHandling.Companion.SUCCESS_BLOG_CREATED
import com.bumptech.glide.RequestManager
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.fragment_create_blog.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import javax.inject.Inject

@MainScope
class CreateBlogFragment @Inject constructor(
    private val viewModelFactory : ViewModelProvider.Factory,
    private val requestManager: RequestManager
): BaseCreateBlogFragment(R.layout.fragment_create_blog){

    val viewModel : CreateBlogViewModel by viewModels {
        viewModelFactory
    }

        override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        cancelActiveJobs()

        // Restore state after process death
        savedInstanceState?.let { inState ->
            (inState[CREATE_BLOG_VIEW_KEY] as CreateBlogViewState?)?.let { viewState ->
                viewModel.setViewState(viewState)
            }
        }
    }


    override fun onSaveInstanceState(outState: Bundle) {
        outState.putParcelable(
            CREATE_BLOG_VIEW_KEY,
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

        blog_image.setOnClickListener {
            if(stateChangeListener.isStoragePermissionGranted()){
                pickFromGallery()
            }
        }

        update_textview.setOnClickListener {
            if(stateChangeListener.isStoragePermissionGranted()){
                pickFromGallery()
            }
        }

        subscribeObservers()
    }

    private fun pickFromGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"

        val mimeTypes = arrayOf("image/jpeg", "image/png", "image/jpg")

        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
        intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION

        //start another activity and receive a result back
        // in this case gives the control to the gallery
        startActivityForResult(intent, GALLERY_REQUEST_CODE)

        //https://github.com/ArthurHub/Android-Image-Cropper
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {

            Log.d(TAG, "CROP: RESULT OK")
            when (requestCode) {

                // requests the crop intent , to see the crop activity
                GALLERY_REQUEST_CODE -> {
                    data?.data?.let { uri ->
                        activity?.let {
                            launchImageCrop(uri)
                        }
                    } ?: showErrorDialog(ERROR_SOMETHING_WRONG_WITH_IMAGE)
                }

                // crop went ok
                CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE -> {
                    Log.d(TAG, "CROP: CROP_IMAGE_ACTIVITY_REQUEST_CODE")
                    val result = CropImage.getActivityResult(data)
                    val resultUri = result.uri

                    Log.d(TAG, "CROP: CROP_IMAGE_ACTIVITY_REQUEST_CODE: uri: ${resultUri}")

                    viewModel.setNewBlogFields(
                        title = null,
                        body = null,
                        uri = resultUri
                    )
                }

                //UPSSS
                CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE -> {
                    Log.d(TAG, "CROP: ERROR")
                    showErrorDialog(ERROR_SOMETHING_WRONG_WITH_IMAGE)
                }

            }
        }
    }


    private fun launchImageCrop(uri: Uri){
        context?.let{
            CropImage.activity(uri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(it, this)
        }
    }

    fun showErrorDialog(errorMessage: String){
        stateChangeListener.onDataStateChange(
            DataState(
                Event(StateError(Response(errorMessage, ResponseType.Dialog))),
                Loading(isLoading = false),
                Data(Event.dataEvent(null), null)
            )
        )
    }

    fun setBlogProperties(title: String?, body: String?, image: Uri?){
        if(image != null){
            requestManager
                .load(image)
                .into(blog_image)
        }
        else{
            requestManager
                .load(R.drawable.default_image)
                .into(blog_image)
        }

        blog_title.setText(title)
        blog_body.setText(body)
    }

   private fun subscribeObservers(){
       viewModel.dataState.observe(viewLifecycleOwner, Observer { dataState ->

           if(dataState != null) {
               stateChangeListener.onDataStateChange(dataState)

               dataState.data?.let { data ->
                   data.response?.let { event ->
                       event.peekContent().let { response ->
                           response.message?.let { message ->

                               if (message == SUCCESS_BLOG_CREATED) {
                                   viewModel.clearNewBlogFields()
                               }
                           }
                       }
                   }
               }
           }
       })

        viewModel.viewState.observe(viewLifecycleOwner, Observer { viewState ->
            viewState.blogFields.let{ newBlogFields ->
                setBlogProperties(
                    newBlogFields.newBlogTitle,
                    newBlogFields.newBlogBody,
                    newBlogFields.newImageUri
                )
            }
        })
    }


    private fun publishNewBlog(){
        var multipartBody: MultipartBody.Part? = null

        viewModel.getNewImageUri()?.let { imageUri ->

            imageUri.path?.let { filePath ->
                val imageFile = File(filePath)

                val requestBody =
                    RequestBody.create(
                        MediaType.parse("image/*"),
                        imageFile
                    )
                // name = field name in serializer
                // filename = name of the image file
                // requestBody = file with file type information
                multipartBody = MultipartBody.Part.createFormData(
                    "image",
                    imageFile.name,
                    requestBody
                )
            }
        }

        multipartBody?.let {

            viewModel.setStateEvent(
                CreateBlogStateEvent.CreateNewBlogEvent(
                    blog_title.text.toString(),
                    blog_body.text.toString(),
                    it
                )
            )
            stateChangeListener.hideSoftKeyboard()
        }?: showErrorDialog(ERROR_MUST_SELECT_IMAGE)

    }

    // to save data if rotating screen
    override fun onPause() {
        super.onPause()

        viewModel.setNewBlogFields(
            blog_title.text.toString(),
            blog_body.text.toString(),
            null
        )
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.publish_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){

            R.id.publish -> {
                val callback: AreYouSureCallback = object: AreYouSureCallback {

                    override fun proceed() {
                        publishNewBlog()
                    }

                    override fun cancel() {
                        // ignore
                    }

                }
                uiCommunicationListener.onUIMessageReceived(
                    UIMessage(
                        getString(R.string.are_you_sure_publish),
                        UIMessageType.AreYouSureDialog(callback)
                    )
                )
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

}