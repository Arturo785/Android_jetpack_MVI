package com.ar.jetpackarchitecture.fragments.main.create_blog

import androidx.fragment.app.FragmentFactory
import androidx.lifecycle.ViewModelProvider
import com.ar.jetpackarchitecture.di.main.MainScope
import com.ar.jetpackarchitecture.ui.main.create_blog.CreateBlogFragment
import com.bumptech.glide.RequestManager
import javax.inject.Inject

@MainScope
class CreateBlogFragmentFactory
@Inject
constructor(
    private val viewModelFactory: ViewModelProvider.Factory,
    private val requestManager: RequestManager
) : FragmentFactory() {

    override fun instantiate(classLoader: ClassLoader, className: String) =

        when (className) {

            CreateBlogFragment::class.java.name -> {
                CreateBlogFragment(viewModelFactory, requestManager)
            }

            else -> {
                CreateBlogFragment(viewModelFactory, requestManager)
            }
        }


}