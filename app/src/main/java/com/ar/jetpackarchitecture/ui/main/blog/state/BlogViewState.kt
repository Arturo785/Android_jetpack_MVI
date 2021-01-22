package com.ar.jetpackarchitecture.ui.main.blog.state

import android.net.Uri
import android.os.Parcelable
import com.ar.jetpackarchitecture.models.BlogPost
import com.ar.jetpackarchitecture.persistence.BlogQueryUtils.Companion.BLOG_ORDER_ASC
import com.ar.jetpackarchitecture.persistence.BlogQueryUtils.Companion.ORDER_BY_ASC_DATE_UPDATED
import kotlinx.android.parcel.Parcelize

const val BLOG_VIEW_STATE_BUNDLE_KEY = "BLOG_VIEW_STATE_KEY"

@Parcelize
data class BlogViewState (

    // BlogFragment vars
    var blogFields: BlogFields = BlogFields(), // has an empty blogFields that has a emptyList and
            // an empty searchQuery

    var viewBlogFields: ViewBlogFields = ViewBlogFields(),


    // UpdateBlogFragment vars
    var updatedBlogFields: UpdatedBlogFields = UpdatedBlogFields()


) : Parcelable {
    // each one in here represents a fragment inside the blogNavGraph

    @Parcelize
    data class BlogFields(
        var blogList: List<BlogPost> = ArrayList<BlogPost>(),
        var searchQuery: String = "",
        var page : Int = 1,
        var isQueryInProgress : Boolean = false,
        var isQueryExhausted : Boolean = false,
        var filter : String = ORDER_BY_ASC_DATE_UPDATED,
        var order : String = BLOG_ORDER_ASC,
        var layoutManagerState : Parcelable? = null
    ) : Parcelable

    @Parcelize
    data class ViewBlogFields(
        var blogPost : BlogPost? = null,
        var isTheAuthorOfBlog : Boolean = false
    ) : Parcelable

    @Parcelize
    data class UpdatedBlogFields(
        var updatedBlogTitle: String? = null,
        var updatedBlogBody: String? = null,
        var updatedImageUri: Uri? = null
    ) : Parcelable

}