package com.ar.jetpackarchitecture.ui.main.blog

import android.app.SearchManager
import android.content.Context.SEARCH_SERVICE
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.EditorInfo.IME_ACTION_SEARCH
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.ar.jetpackarchitecture.R
import kotlinx.android.synthetic.main.fragment_blog.*
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.ar.jetpackarchitecture.di.main.MainScope
import com.ar.jetpackarchitecture.models.BlogPost
import com.ar.jetpackarchitecture.persistence.BlogQueryUtils.Companion.BLOG_FILTER_DATE_UPDATED
import com.ar.jetpackarchitecture.persistence.BlogQueryUtils.Companion.BLOG_FILTER_USERNAME
import com.ar.jetpackarchitecture.persistence.BlogQueryUtils.Companion.BLOG_ORDER_ASC
import com.ar.jetpackarchitecture.ui.DataState
import com.ar.jetpackarchitecture.ui.main.blog.state.BLOG_VIEW_STATE_BUNDLE_KEY
import com.ar.jetpackarchitecture.ui.main.blog.state.BlogViewState
import com.ar.jetpackarchitecture.ui.main.blog.viewmodel.*
import com.ar.jetpackarchitecture.util.TopSpacingItemDecoration
import com.ar.jetpackarchitecture.util.isPaginationDone
import com.bumptech.glide.RequestManager
import javax.inject.Inject

@MainScope
class BlogFragment @Inject constructor(
    private val viewModelFactory : ViewModelProvider.Factory,
    private val requestManager: RequestManager
) : BaseBlogFragment(R.layout.fragment_blog), BlogListAdapter.Interaction,
    SwipeRefreshLayout.OnRefreshListener{


    private lateinit var recyclerAdapter: BlogListAdapter
    private lateinit var searchView: SearchView


    val viewModel : BlogViewModel by viewModels{
        viewModelFactory
    }

        override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        cancelActiveJobs()

        // Restore state after process death
        savedInstanceState?.let { inState ->
            (inState[BLOG_VIEW_STATE_BUNDLE_KEY] as BlogViewState?)?.let { viewState ->
                viewModel.setViewState(viewState)
            }
        }
    }


    override fun onSaveInstanceState(outState: Bundle) {
        val viewState = viewModel.viewState.value

        //clear the list. Don't want to save a large list to bundle.
        viewState?.blogFields?.blogList = ArrayList()

        outState.putParcelable(
            BLOG_VIEW_STATE_BUNDLE_KEY,
            viewState
        )

    super.onSaveInstanceState(outState)
    }

    override fun cancelActiveJobs() {
        viewModel.cancelActiveJobs()
    }


    private fun saveLayoutManagerState(){
        blog_post_recyclerview.layoutManager?.onSaveInstanceState()?.let {state ->
            viewModel.setLayoutManagerState(state)
        }
    }

    override fun onPause() {
        super.onPause()
        saveLayoutManagerState() // to trigger whenever the user leaves the fragment
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(false)

        setHasOptionsMenu(true)
        swipe_refresh.setOnRefreshListener(this)

        
        initRecyclerView()
        subscribeObservers()


    }


    override fun onResume() {
        super.onResume()
        viewModel.refreshFromCache()
    }


    private fun subscribeObservers(){
        viewModel.dataState.observe(viewLifecycleOwner, Observer{ dataState ->
            if(dataState != null){
                handlePagination(dataState)
                stateChangeListener.onDataStateChange(dataState)
            }
        })

        viewModel.viewState.observe(viewLifecycleOwner, Observer{ viewState ->
            Log.d(TAG, "BlogFragment, ViewState: ${viewState}")
            if(viewState != null){
                recyclerAdapter.apply {
                    preloadGlideImages(requestManager, viewState.blogFields.blogList)

                    submitList(
                        viewState.blogFields.blogList,
                        viewState.blogFields.isQueryExhausted
                    )
                }
            }
        })
    }

    private fun handlePagination(dataState: DataState<BlogViewState>){

        // Handle incoming data from DataState
        dataState.data?.let {
            it.data?.let{
                it.getContentIfNotHandled()?.let{
                    viewModel.handleIncomingBlogListData(it)
                }
            }
        }

        // Check for pagination end (no more results)
        // must do this b/c server will return an ApiErrorResponse if page is not valid,
        // -> meaning there is no more data.
        dataState.error?.let{ event ->
            event.peekContent().response.message?.let{
                if(isPaginationDone(it)){

                    // handle the error message event so it doesn't display in UI
                    event.getContentIfNotHandled()

                    // set query exhausted to update RecyclerView with
                    // "No more results..." list item
                    viewModel.setQueryExhausted(true)
                }
            }
        }
    }

    private fun resetUI(){
        blog_post_recyclerview.smoothScrollToPosition(0)
        stateChangeListener.hideSoftKeyboard()
        focusable_view.requestFocus()
    }

    private fun onBlogSearchOrFilter(){
        viewModel.loadFirstPage().let {
            resetUI()
        }
    }

    private fun initSearchView(menu : Menu){
        activity?.apply {
            val searchManager : SearchManager = getSystemService(SEARCH_SERVICE) as
                    SearchManager

            searchView = menu.findItem(R.id.action_search).actionView as SearchView
            searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
            searchView.maxWidth = Integer.MAX_VALUE
            searchView.setIconifiedByDefault(true) // the lens or the close icon
            searchView.isSubmitButtonEnabled = true
        }

        // ENTER ON COMPUTER KEYBOARD OR ARROW ON KEYBOARD
        val searchPlate = searchView.findViewById(R.id.search_src_text) as EditText
        searchPlate.setOnEditorActionListener { textView, actionId, keyEvent ->
            if(actionId == EditorInfo.IME_ACTION_UNSPECIFIED
                || actionId == EditorInfo.IME_ACTION_SEARCH){

                val searchQuery = textView.text.toString()
                viewModel.setQuery(searchQuery).let {
                    onBlogSearchOrFilter()
                }
            }
            true
        }

        // SEARCH BUTTON CLICKED (in toolbar)
        val searchButton = searchView.findViewById(R.id.search_go_btn) as View

        searchButton.setOnClickListener {

            val searchQuery = searchPlate.text.toString()
            Log.e(TAG, "SearchView: (button) executing search...: ${searchQuery}")

            viewModel.setQuery(searchQuery).let {
                onBlogSearchOrFilter()
            }

        }
    }

    private fun initRecyclerView(){

        val topSpacingItemDecoration = TopSpacingItemDecoration(30)

        blog_post_recyclerview.apply {
            layoutManager = LinearLayoutManager(this@BlogFragment.context)
            recyclerAdapter = BlogListAdapter(requestManager, this@BlogFragment)
            addItemDecoration(topSpacingItemDecoration)

            addOnScrollListener(object : RecyclerView.OnScrollListener(){

                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)

                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val lastPos = layoutManager.findLastVisibleItemPosition()

                    if(lastPos == recyclerAdapter.itemCount.minus(1)){
                        viewModel.nextPage()
                    }
                }
            })

            adapter = recyclerAdapter
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        // cleaning references
        blog_post_recyclerview.adapter = null
    }

    override fun onItemSelected(position: Int, item: BlogPost) {
        viewModel.setBlogPost(item)
        findNavController().navigate(R.id.action_blogFragment_to_viewBlogFragment)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.search_menu, menu)
        initSearchView(menu)
    }

    override fun onRefresh() {
        onBlogSearchOrFilter()
        swipe_refresh.isRefreshing = false
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){

            R.id.action_filter_settings -> {
                showFilterDialog()
                return true
            }

        }

        return super.onOptionsItemSelected(item)
    }

    fun showFilterDialog(){

        activity?.let {
            val dialog = MaterialDialog(it)
                .noAutoDismiss()
                .customView(R.layout.layout_blog_filter)

            val view = dialog.getCustomView()

            val filter = viewModel.getFilter()
            val order = viewModel.getOrder()

            // which radio to select on filter
            if(filter.equals(BLOG_FILTER_DATE_UPDATED)){
                view.findViewById<RadioGroup>(R.id.filter_group).check(R.id.filter_date)
            }
            else{
                view.findViewById<RadioGroup>(R.id.filter_group).check(R.id.filter_author)
            }

            // which radio to select on order
            if(order.equals(BLOG_ORDER_ASC)){
                view.findViewById<RadioGroup>(R.id.order_group).check(R.id.filter_asc)
            }
            else{
                view.findViewById<RadioGroup>(R.id.order_group).check(R.id.filter_desc)
            }

            // when accept dialog
            view.findViewById<TextView>(R.id.positive_button).setOnClickListener{
                Log.d(TAG, "FilterDialog: apply filter.")

                val selectedFilter = dialog.getCustomView().findViewById<RadioButton>(
                    dialog.getCustomView().findViewById<RadioGroup>(R.id.filter_group).checkedRadioButtonId
                )
                val selectedOrder= dialog.getCustomView().findViewById<RadioButton>(
                    dialog.getCustomView().findViewById<RadioGroup>(R.id.order_group).checkedRadioButtonId
                )

                var filter = BLOG_FILTER_DATE_UPDATED
                if(selectedFilter.text.toString().equals(getString(R.string.filter_author))){
                    filter = BLOG_FILTER_USERNAME
                }

                var order = ""
                if(selectedOrder.text.toString().equals(getString(R.string.filter_desc))){
                    order = "-"
                }
                viewModel.saveFilterOptions(filter, order).let{
                    viewModel.setBlogFilter(filter)
                    viewModel.setBlogOrder(order)
                    onBlogSearchOrFilter()
                }
                dialog.dismiss()
            }

            view.findViewById<TextView>(R.id.negative_button).setOnClickListener {
                Log.d(TAG, "FilterDialog: cancelling filter.")
                dialog.dismiss()
            }

            dialog.show()
        }
    }

    override fun restoreListPosition() {
        viewModel.viewState.value?.blogFields?.layoutManagerState?.let {state ->
            blog_post_recyclerview?.layoutManager?.onRestoreInstanceState(state)
        }
    }
}