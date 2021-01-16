package com.ar.jetpackarchitecture.di.main

import com.ar.jetpackarchitecture.api.main.OpenApiMainService
import com.ar.jetpackarchitecture.di.AppModule_ProvidesRetrofitBuilderFactory
import com.ar.jetpackarchitecture.persistence.AccountPropertiesDAO
import com.ar.jetpackarchitecture.persistence.AppDatabase
import com.ar.jetpackarchitecture.persistence.AuthTokenDAO
import com.ar.jetpackarchitecture.persistence.BlogPostDAO
import com.ar.jetpackarchitecture.repository.main.AccountRepository
import com.ar.jetpackarchitecture.repository.main.BlogRepository
import com.ar.jetpackarchitecture.repository.main.CreateBlogRepository
import com.ar.jetpackarchitecture.session.SessionManager
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit

@Module
class MainModule {

    @MainScope
    @Provides
    fun provideOpenApiMainService(retrofitBuilder : Retrofit.Builder) : OpenApiMainService{
        return retrofitBuilder
            .build()
            .create(OpenApiMainService::class.java)
    }

    @MainScope
    @Provides
    fun provideMainAccountRepository(
        openApiMainService : OpenApiMainService,
        accountPropertiesDAO: AccountPropertiesDAO,
        sessionManager: SessionManager
    ) : AccountRepository{
        return AccountRepository(
            openApiMainService,
            accountPropertiesDAO,
            sessionManager
        )
    }

    @MainScope
    @Provides
    fun provideBlogPostDao(db: AppDatabase): BlogPostDAO {
        return db.getBlogPostDAO()
    }

    @MainScope
    @Provides
    fun provideBlogRepository(
        openApiMainService : OpenApiMainService,
        blogPostDAO: BlogPostDAO,
        sessionManager: SessionManager
    ) : BlogRepository{
        return BlogRepository(
            openApiMainService,
            blogPostDAO,
            sessionManager
        )
    }

    @MainScope
    @Provides
    fun provideCreateBlogRepository(
        openApiMainService : OpenApiMainService,
        blogPostDAO: BlogPostDAO,
        sessionManager: SessionManager
    ) : CreateBlogRepository{
        return CreateBlogRepository(
            openApiMainService,
            blogPostDAO,
            sessionManager
        )
    }

}