package com.ar.jetpackarchitecture.repository.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.switchMap
import com.ar.jetpackarchitecture.api.GenericResponse
import com.ar.jetpackarchitecture.api.main.OpenApiMainService
import com.ar.jetpackarchitecture.api.main.responses.BlogCreateUpdateResponse
import com.ar.jetpackarchitecture.api.main.responses.BlogListSearchResponse
import com.ar.jetpackarchitecture.di.main.MainScope
import com.ar.jetpackarchitecture.models.AuthToken
import com.ar.jetpackarchitecture.models.BlogPost
import com.ar.jetpackarchitecture.persistence.BlogPostDAO
import com.ar.jetpackarchitecture.persistence.returnOrderedBlogQuery
import com.ar.jetpackarchitecture.repository.JobManager
import com.ar.jetpackarchitecture.repository.NetworkBoundResource
import com.ar.jetpackarchitecture.session.SessionManager
import com.ar.jetpackarchitecture.ui.DataState
import com.ar.jetpackarchitecture.ui.Response
import com.ar.jetpackarchitecture.ui.ResponseType
import com.ar.jetpackarchitecture.ui.main.blog.state.BlogViewState
import com.ar.jetpackarchitecture.util.*
import com.ar.jetpackarchitecture.util.Constants.Companion.PAGINATION_PAGE_SIZE
import com.ar.jetpackarchitecture.util.SuccessHandling.Companion.RESPONSE_HAS_PERMISSION_TO_EDIT
import com.ar.jetpackarchitecture.util.SuccessHandling.Companion.RESPONSE_NO_PERMISSION_TO_EDIT
import com.ar.jetpackarchitecture.util.SuccessHandling.Companion.SUCCESS_BLOG_DELETED
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.lang.Exception
import javax.inject.Inject


@MainScope
class BlogRepository @Inject constructor(
    val openApiMainService: OpenApiMainService,
    val blogPostDAO: BlogPostDAO,
    val sessionManager : SessionManager
): JobManager("BlogRepository") {

     private val TAG = "BlogRepository"

    fun searchBlogPosts(
        authToken: AuthToken,
        query : String,
        filterAndOrder : String,
        page : Int
    ): LiveData<DataState<BlogViewState>> {
        return object : NetworkBoundResource<BlogListSearchResponse, List<BlogPost>, BlogViewState>(
            sessionManager.isConnectedToInternet(),
            true,
            true,
            false
        ){

            override suspend fun handleAPISuccessResponse(response: ApiSuccessResponse<BlogListSearchResponse>) {

                val myBlogPostList = response.body.results.map {
                     BlogPost(
                        it.pk,
                        it.title,
                        it.slug,
                        it.body,
                        it.image,
                        DateUtils.convertServerStringDateToLong(it.date_updated),
                        it.username
                        )
                }

                updateLocalDb(myBlogPostList)
                createCacheRequestAndReturn()
            }

            override fun createCall(): LiveData<GenericApiResponse<BlogListSearchResponse>> {
                return openApiMainService.searchListBlogPosts(
                    "Token ${authToken.token}",
                    query,
                    ordering = filterAndOrder,
                    page = page,
                )
            }

            override fun setJob(job: Job) {
                addJob("searchBlogPosts", job)
            }

            override fun loadFromCache(): LiveData<BlogViewState> {
                return blogPostDAO.returnOrderedBlogQuery(
                    query = query,
                    filterAndOrder = filterAndOrder,
                    page = page
                )
                    .switchMap {
                        object : LiveData<BlogViewState>(){

                            override fun onActive() {
                                super.onActive()
                                value = BlogViewState(
                                    BlogViewState.BlogFields(
                                        blogList = it,
                                        isQueryInProgress = true
                                    )
                                )
                            }
                        }
                    }
            }

            override suspend fun updateLocalDb(cachedObject: List<BlogPost>?) {
                if(cachedObject != null){
                    withContext(IO){
                        cachedObject.forEach {blogPost ->
                            try {
                                // launch each insert as a separate job to be executed in parallel
                                // every blogPost is an unique coroutine
                                launch {
                                    Log.d(TAG, "updateLocalDb: inserting $blogPost")
                                    blogPostDAO.insert(blogPost)
                                }
                            }
                            catch (e : Exception){
                                Log.e(TAG, "updateLocalDb: error updating cache on blog with " +
                                        "slug ${blogPost.slug}", )
                            }
                        }
                    }
                }
            }

            override suspend fun createCacheRequestAndReturn() {
                withContext(Main){

                    result.addSource(loadFromCache()){viewState ->

                        viewState.blogFields.isQueryInProgress = false

                        if(page * PAGINATION_PAGE_SIZE > viewState.blogFields.blogList.size){
                            viewState.blogFields.isQueryExhausted = true
                        }

                        onCompleteJob(DataState.data(
                            viewState,
                            null
                        ))
                    }
                }

            }

        }.asLiveData()
    }

    fun isAuthorOfBlogPost(
        authToken: AuthToken,
        slug: String
    ): LiveData<DataState<BlogViewState>> {
        return object: NetworkBoundResource<GenericResponse, Any, BlogViewState>(
            sessionManager.isConnectedToInternet(),
            true,
            false,
            true
        ){


            // not applicable
            override suspend fun createCacheRequestAndReturn() {

            }

            override suspend fun handleAPISuccessResponse(response: ApiSuccessResponse<GenericResponse>) {
                withContext(Main){

                    Log.d(TAG, "handleApiSuccessResponse: ${response.body.response}")
                    val isAuthor = (response.body.response == RESPONSE_HAS_PERMISSION_TO_EDIT)

                    onCompleteJob(
                        DataState.data(
                            data = BlogViewState(
                                viewBlogFields = BlogViewState.ViewBlogFields(
                                    isTheAuthorOfBlog = isAuthor
                                )
                            ),
                            response = null
                        )
                    )
                }
            }

            // not applicable
            override fun loadFromCache(): LiveData<BlogViewState> {
                return AbsentLiveData.create()
            }

            // Make an update and change nothing.
            // If they are not the author it will return: "You don't have permission to edit that."
            override fun createCall(): LiveData<GenericApiResponse<GenericResponse>> {
                return openApiMainService.isAuthorOfBlogPost(
                    "Token ${authToken.token!!}",
                    slug
                )
            }

            // not applicable
            override suspend fun updateLocalDb(cacheObject: Any?) {

            }

            override fun setJob(job: Job) {
                addJob("isAuthorOfBlogPost", job)
            }


        }.asLiveData()
    }

    fun deleteBlogPost(
        authToken: AuthToken,
        blogPost: BlogPost
    ): LiveData<DataState<BlogViewState>>{
        return object: NetworkBoundResource<GenericResponse, BlogPost, BlogViewState>(
            sessionManager.isConnectedToInternet(),
            true,
            false,
            true
        ){

            // not applicable
            override suspend fun createCacheRequestAndReturn() {

            }

            override suspend fun handleAPISuccessResponse(response: ApiSuccessResponse<GenericResponse>) {

                if(response.body.response == SUCCESS_BLOG_DELETED){
                    updateLocalDb(blogPost)
                }
                else{
                    onCompleteJob(
                        DataState.error(
                            Response(
                                ERROR_UNKNOWN,
                                ResponseType.Dialog
                            )
                        )
                    )
                }
            }

            // not applicable
            override fun loadFromCache(): LiveData<BlogViewState> {
                return AbsentLiveData.create()
            }

            override fun createCall(): LiveData<GenericApiResponse<GenericResponse>> {
                return openApiMainService.deleteBlogPost(
                    "Token ${authToken.token!!}",
                    blogPost.slug
                )
            }

            override suspend fun updateLocalDb(cacheObject: BlogPost?) {
                cacheObject?.let{blogPost ->
                    blogPostDAO.deleteBlogPost(blogPost)
                    onCompleteJob(
                        DataState.data(
                            null,
                            Response(SUCCESS_BLOG_DELETED, ResponseType.Toast)
                        )
                    )
                }
            }

            override fun setJob(job: Job) {
                addJob("deleteBlogPost", job)
            }

        }.asLiveData()
    }

    fun updateBlogPost(
        authToken: AuthToken,
        slug: String,
        title: RequestBody,
        body: RequestBody,
        image: MultipartBody.Part?
    ): LiveData<DataState<BlogViewState>> {
        return object: NetworkBoundResource<BlogCreateUpdateResponse, BlogPost, BlogViewState>(
            sessionManager.isConnectedToInternet(),
            true,
            false,
            true
        ){

            // not applicable
            override suspend fun createCacheRequestAndReturn() {

            }

            override suspend fun handleAPISuccessResponse(
                response: ApiSuccessResponse<BlogCreateUpdateResponse>
            ) {

                val updatedBlogPost = BlogPost(
                    response.body.pk,
                    response.body.title,
                    response.body.slug,
                    response.body.body,
                    response.body.image,
                    DateUtils.convertServerStringDateToLong(response.body.date_updated),
                    response.body.username
                )

                updateLocalDb(updatedBlogPost)

                withContext(Dispatchers.Main){
                    // finish with success response
                    onCompleteJob(
                        DataState.data(
                            BlogViewState(
                                viewBlogFields = BlogViewState.ViewBlogFields(
                                    blogPost = updatedBlogPost
                                )
                            ),
                            Response(response.body.response, ResponseType.Toast)
                        ))
                }
            }

            // not applicable
            override fun loadFromCache(): LiveData<BlogViewState> {
                return AbsentLiveData.create()
            }

            override fun createCall(): LiveData<GenericApiResponse<BlogCreateUpdateResponse>> {
                return openApiMainService.updateBlog(
                    "Token ${authToken.token!!}",
                    slug,
                    title,
                    body,
                    image
                )
            }

            override suspend fun updateLocalDb(cacheObject: BlogPost?) {
                cacheObject?.let{blogPost ->
                    blogPostDAO.updateBlogPost(
                        blogPost.pk,
                        blogPost.title,
                        blogPost.body,
                        blogPost.image
                    )
                }
            }

            override fun setJob(job: Job) {
                addJob("updateBlogPost", job)
            }

        }.asLiveData()
    }

    fun restoreBlogListFromCache(
        query: String,
        filterAndOrder: String,
        page: Int
    ): LiveData<DataState<BlogViewState>> {
        return object: NetworkBoundResource<BlogListSearchResponse, List<BlogPost>, BlogViewState>(
            sessionManager.isConnectedToInternet(),
            false,
            true,
            false
        ) {
            override suspend fun createCacheRequestAndReturn() {
                withContext(Dispatchers.Main){
                    result.addSource(loadFromCache()){ viewState ->
                        viewState.blogFields.isQueryInProgress = false
                        if(page * PAGINATION_PAGE_SIZE > viewState.blogFields.blogList.size){
                            viewState.blogFields.isQueryExhausted = true
                        }
                        onCompleteJob(DataState.data(
                            viewState,
                            null
                        ))
                    }
                }
            }

            override suspend fun handleAPISuccessResponse(
                response: ApiSuccessResponse<BlogListSearchResponse>
            ) {
                // ignore
            }

            override fun createCall(): LiveData<GenericApiResponse<BlogListSearchResponse>> {
                return AbsentLiveData.create()
            }

            override fun loadFromCache(): LiveData<BlogViewState> {
                return blogPostDAO.returnOrderedBlogQuery(
                    query = query,
                    filterAndOrder = filterAndOrder,
                    page = page)
                    .switchMap {
                        object: LiveData<BlogViewState>(){
                            override fun onActive() {
                                super.onActive()
                                value = BlogViewState(
                                    BlogViewState.BlogFields(
                                        blogList = it,
                                        isQueryInProgress = true
                                    )
                                )
                            }
                        }
                    }
            }

            override suspend fun updateLocalDb(cacheObject: List<BlogPost>?) {
                // ignore
            }

            override fun setJob(job: Job) {
                addJob("restoreBlogListFromCache", job)
            }

        }.asLiveData()
    }




}