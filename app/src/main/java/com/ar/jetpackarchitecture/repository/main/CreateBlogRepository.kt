package com.ar.jetpackarchitecture.repository.main

import com.ar.jetpackarchitecture.di.main.MainScope
import com.ar.jetpackarchitecture.models.AuthToken
import com.ar.jetpackarchitecture.ui.main.create_blog.state.CreateBlogViewState
import com.ar.jetpackarchitecture.util.DataState
import com.ar.jetpackarchitecture.util.StateEvent
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.RequestBody

@FlowPreview
@MainScope
interface CreateBlogRepository {

    fun createNewBlogPost(
        authToken: AuthToken,
        title: RequestBody,
        body: RequestBody,
        image: MultipartBody.Part?,
        stateEvent: StateEvent
    ): Flow<DataState<CreateBlogViewState>>
}