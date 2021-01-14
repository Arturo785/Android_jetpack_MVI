package com.ar.jetpackarchitecture.ui.main.blog.state

import android.net.Uri
import com.ar.jetpackarchitecture.models.BlogPost
import com.ar.jetpackarchitecture.persistence.BlogQueryUtils.Companion.BLOG_ORDER_ASC
import com.ar.jetpackarchitecture.persistence.BlogQueryUtils.Companion.ORDER_BY_ASC_DATE_UPDATED

data class BlogViewState (

    // BlogFragment vars
    var blogFields: BlogFields = BlogFields(), // has an empty blogFields that has a emptyList and
            // an empty searchQuery

    var viewBlogFields: ViewBlogFields = ViewBlogFields(),


    // UpdateBlogFragment vars
    var updatedBlogFields: UpdatedBlogFields = UpdatedBlogFields()


)
{
    // each one in here represents a fragment inside the blogNavGraph
    data class BlogFields(
        var blogList: List<BlogPost> = ArrayList<BlogPost>(),
        var searchQuery: String = "",
        var page : Int = 1,
        var isQueryInProgress : Boolean = false,
        var isQueryExhausted : Boolean = false,
        var filter : String = ORDER_BY_ASC_DATE_UPDATED,
        var order : String = BLOG_ORDER_ASC
    )

    data class ViewBlogFields(
        var blogPost : BlogPost? = null,
        var isTheAuthorOfBlog : Boolean = false
    )

    data class UpdatedBlogFields(
        var updatedBlogTitle: String? = null,
        var updatedBlogBody: String? = null,
        var updatedImageUri: Uri? = null
    )

}