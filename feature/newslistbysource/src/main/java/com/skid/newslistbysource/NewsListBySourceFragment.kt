package com.skid.newslistbysource

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.skid.newslist.databinding.FragmentNewsListBySourceBinding
import com.skid.newslistbysource.di.NewsListBySourceComponentViewModel
import com.skid.paging.PagingAdapter
import com.skid.ui.databinding.ArticleItemBinding
import com.skid.ui.onSearchItemCollapseInDarkTheme
import com.skid.ui.searchItemOnActionExpandListener
import com.skid.utils.Constants
import com.skid.utils.Constants.SOURCE_ID_KEY
import com.skid.utils.Constants.SOURCE_NAME_KEY
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Provider

class NewsListBySourceFragment : Fragment() {

    private var _binding: FragmentNewsListBySourceBinding? = null
    private val binding get() = checkNotNull(_binding)

    @Inject
    lateinit var viewModelProvider: Provider<NewsListBySourceViewModel.Factory>
    private val newsListBySourceViewModel: NewsListBySourceViewModel by viewModels {
        NewsListBySourceViewModel.viewModelFactory(
            assistedFactory = viewModelProvider.get(),
            sourceId = checkNotNull(requireArguments().getString(SOURCE_ID_KEY))
        )
    }

    @Inject
    lateinit var router: NewsListBySourceRouter

    private val disposables = CompositeDisposable()
    private var searchDisposable: Disposable? = null

    private val newsAdapter by lazy {
        PagingAdapter(
            diffCallback = ArticleDiffCallback(),
            binding = { layoutInflater, parent ->
                ArticleItemBinding.inflate(layoutInflater, parent, false)
            },
            bind = { article ->
                root.setOnClickListener { router.toArticleProfile(article) }
                articleItemImage.load(article.imageUrl) {
                    crossfade(true)
                }
                articleItemSourceImage.setImageResource(article.sourceDrawableId)
                articleItemSourceName.text = article.sourceName
                articleItemTitle.text = article.title
            },
            doOnError = router::onError
        )
    }

    private val newsSearchAdapter by lazy {
        PagingAdapter(
            diffCallback = ArticleDiffCallback(),
            binding = { layoutInflater, parent ->
                ArticleItemBinding.inflate(layoutInflater, parent, false)
            },
            bind = { article ->
                root.setOnClickListener {
                    onSearchItemCollapseInDarkTheme()
                    router.toArticleProfile(article)
                }
                articleItemImage.load(article.imageUrl) {
                    crossfade(true)
                }
                articleItemSourceImage.setImageResource(article.sourceDrawableId)
                articleItemSourceName.text = article.sourceName
                articleItemTitle.text = article.title
            }
        )
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        ViewModelProvider(this)
            .get<NewsListBySourceComponentViewModel>()
            .newsListBySourceComponent
            .inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentNewsListBySourceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupErrorResultListener()
        setupToolbar()
        setupRecyclerView()
        setupSwipeRefresh()
        setupNewsPagerObserver()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        disposables.clear()
        _binding = null
    }

    private fun setupRecyclerView() = with(binding) {
        newsListBySourceRecyclerView.layoutManager = LinearLayoutManager(context)
        newsListBySourceRecyclerView.adapter = newsAdapter
        newsListBySourceRecyclerView
            .addItemDecoration(DividerItemDecoration(context, RecyclerView.VERTICAL))
        newsAdapter.addOnLoadMoreListener(newsListBySourceViewModel::onLoadNextPage)

        newsListBySourceSearchRecyclerView.layoutManager = LinearLayoutManager(context)
        newsListBySourceSearchRecyclerView.adapter = newsSearchAdapter
        newsListBySourceSearchRecyclerView
            .addItemDecoration(DividerItemDecoration(context, RecyclerView.VERTICAL))
        newsSearchAdapter.addOnLoadMoreListener(newsListBySourceViewModel::onLoadNextPageForSearch)
    }

    private fun setupToolbar() {
        (requireActivity() as AppCompatActivity).supportActionBar?.apply {
            title = requireArguments().getString(SOURCE_NAME_KEY)
            setDisplayHomeAsUpEnabled(true)
        }

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(menuProvider, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private val menuProvider = object : MenuProvider {
        override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
            val searchItem = menu.findItem(com.skid.ui.R.id.main_activity_menu_search)
            (searchItem.actionView as SearchView).setOnQueryTextListener(
                object : SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String?): Boolean = true

                    override fun onQueryTextChange(newText: String?): Boolean {
                        if (newText != null) {
                            newsListBySourceViewModel.onQueryChanged(newText)
                        }
                        return true
                    }
                }
            )
            with(binding) {
                searchItem.setOnActionExpandListener(
                    searchItemOnActionExpandListener(
                        onExpand = {
                            searchDisposable = newsListBySourceViewModel
                                .searchPagerObservable
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(newsSearchAdapter::submitPage)

                            newsListBySourceSwipeRefreshLayout.isVisible = false
                            newsListBySourceSearchRecyclerView.isVisible = true
                        },
                        onCollapse = {
                            newsListBySourceSwipeRefreshLayout.isVisible = true
                            newsListBySourceSearchRecyclerView.isVisible = false
                            searchDisposable?.dispose()
                        }
                    )
                )
            }
        }

        override fun onMenuItemSelected(menuItem: MenuItem): Boolean = true
    }

    private fun setupSwipeRefresh() = with(binding) {
        newsListBySourceSwipeRefreshLayout.setOnRefreshListener {
            newsListBySourceViewModel.refreshPager()
        }
    }

    private fun setupNewsPagerObserver() = with(binding) {
        val disposable = newsListBySourceViewModel
            .pagingDataReplaySubject
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { pagingData ->
                newsAdapter.submitPage(pagingData)
                newsListBySourceSwipeRefreshLayout.isRefreshing = false
                newsListBySourceProgressBar.isVisible = false
                newsListBySourceSwipeRefreshLayout.isVisible = true
            }

        disposables.add(disposable)
    }

    private fun setupErrorResultListener() {
        parentFragmentManager.setFragmentResultListener(
            Constants.ERROR_RESULT_KEY,
            viewLifecycleOwner
        ) { _, _ -> newsListBySourceViewModel.refreshPager() }
    }

}