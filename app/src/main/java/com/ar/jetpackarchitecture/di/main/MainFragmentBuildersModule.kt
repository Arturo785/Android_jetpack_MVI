package com.ar.jetpackarchitecture.di.main

import com.ar.jetpackarchitecture.ui.main.account.AccountFragment
import com.ar.jetpackarchitecture.ui.main.account.ChangePasswordFragment
import com.ar.jetpackarchitecture.ui.main.account.UpdateAccountFragment
import com.ar.jetpackarchitecture.ui.main.blog.BlogFragment
import com.ar.jetpackarchitecture.ui.main.blog.UpdateBlogFragment
import com.ar.jetpackarchitecture.ui.main.blog.ViewBlogFragment
import com.ar.jetpackarchitecture.ui.main.create_blog.CreateBlogFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class MainFragmentBuildersModule {

    @ContributesAndroidInjector()
    abstract fun contributeBlogFragment(): BlogFragment

    @ContributesAndroidInjector()
    abstract fun contributeAccountFragment(): AccountFragment

    @ContributesAndroidInjector()
    abstract fun contributeChangePasswordFragment(): ChangePasswordFragment

    @ContributesAndroidInjector()
    abstract fun contributeCreateBlogFragment(): CreateBlogFragment

    @ContributesAndroidInjector()
    abstract fun contributeUpdateBlogFragment(): UpdateBlogFragment

    @ContributesAndroidInjector()
    abstract fun contributeViewBlogFragment(): ViewBlogFragment

    @ContributesAndroidInjector()
    abstract fun contributeUpdateAccountFragment(): UpdateAccountFragment
}