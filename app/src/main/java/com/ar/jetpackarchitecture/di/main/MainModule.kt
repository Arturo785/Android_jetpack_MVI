package com.ar.jetpackarchitecture.di.main

import com.ar.jetpackarchitecture.api.main.OpenApiMainService
import com.ar.jetpackarchitecture.persistence.AccountPropertiesDAO
import com.ar.jetpackarchitecture.persistence.AppDatabase
import com.ar.jetpackarchitecture.persistence.BlogPostDAO
import com.ar.jetpackarchitecture.repository.main.*
import com.ar.jetpackarchitecture.session.SessionManager
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.FlowPreview
import retrofit2.Retrofit

@Module
object MainModule {

    @JvmStatic
    @MainScope
    @Provides
    fun provideOpenApiMainService(retrofitBuilder : Retrofit.Builder) : OpenApiMainService{
        return retrofitBuilder
            .build()
            .create(OpenApiMainService::class.java)
    }

    @FlowPreview
    @JvmStatic
    @MainScope
    @Provides
    fun provideMainAccountRepository(
        openApiMainService : OpenApiMainService,
        accountPropertiesDAO: AccountPropertiesDAO,
        sessionManager: SessionManager
    ) : AccountRepositoryImpl{
        return AccountRepositoryImpl(
            openApiMainService,
            accountPropertiesDAO,
            sessionManager
        )
    }

    @JvmStatic
    @MainScope
    @Provides
    fun provideBlogPostDao(db: AppDatabase): BlogPostDAO {
        return db.getBlogPostDAO()
    }

    @FlowPreview
    @JvmStatic
    @MainScope
    @Provides
    fun provideBlogRepository(
        openApiMainService : OpenApiMainService,
        blogPostDAO: BlogPostDAO,
        sessionManager: SessionManager
    ) : BlogRepository {
        return BlogRepositoryImpl(
            openApiMainService,
            blogPostDAO,
            sessionManager
        )
    }

    @FlowPreview
    @JvmStatic
    @MainScope
    @Provides
    fun provideCreateBlogRepository(
        openApiMainService : OpenApiMainService,
        blogPostDAO: BlogPostDAO,
        sessionManager: SessionManager
    ) : CreateBlogRepository {
        return CreateBlogRepositoryImpl(
            openApiMainService,
            blogPostDAO,
            sessionManager
        )
    }

}