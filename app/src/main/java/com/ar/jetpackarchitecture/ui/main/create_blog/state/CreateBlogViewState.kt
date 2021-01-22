package com.ar.jetpackarchitecture.ui.main.create_blog.state

import android.net.Uri
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


const val CREATE_BLOG_VIEW_KEY = "CREATE_BLOG_VIEW_KEY"

@Parcelize
data class CreateBlogViewState(

    // CreateBlogFragment vars
    var blogFields: NewBlogFields = NewBlogFields()

) : Parcelable {

    @Parcelize
    data class NewBlogFields(
        var newBlogTitle: String? = null,
        var newBlogBody: String? = null,
        var newImageUri: Uri? = null
    ) : Parcelable
}