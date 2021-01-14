package com.ar.jetpackarchitecture.ui.main.blog.state

import okhttp3.MultipartBody

sealed class BlogStateEvent {

    object BlogSearchEvent : BlogStateEvent()

    object CheckAuthorOfBlogPost : BlogStateEvent()

    object DeleteBlogPostEvent : BlogStateEvent()

    data class UpdateBlogPostEvent(
        val title: String,
        val body: String,
        val image: MultipartBody.Part?
    ): BlogStateEvent()

    object None: BlogStateEvent()
}