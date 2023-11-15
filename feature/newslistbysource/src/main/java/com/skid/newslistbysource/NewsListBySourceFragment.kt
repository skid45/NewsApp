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
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.skid.newslist.databinding.FragmentNewsListBySourceBinding
import com.skid.newslistbysource.di.NewsListBySourceComponentViewModel
import com.skid.paging.PagingAdapter
import com.skid.ui.databinding.ArticleItemBinding
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
    private val newsListBySourceViewModel: NewsListBySourceViewModel by viewModels { viewModelProvider.get() }

    private val disposables = CompositeDisposable()
    private var searchDisposable: Disposable? = null

    private val newsAdapter by lazy {
        PagingAdapter(
            diffCallback = ArticleDiffCallback(),
            binding = { layoutInflater, parent ->
                ArticleItemBinding.inflate(layoutInflater, parent, false)
            },
            bind = { article ->
                articleItemImage.load(article.imageUrl) {
                    crossfade(true)
                }
                articleItemSourceImage.setImageResource(article.sourceDrawableId)
                articleItemSourceName.text = article.sourceName
                articleItemTitle.text = article.title
            }
        )
    }

    private val newsSearchAdapter by lazy {
        PagingAdapter(
            diffCallback = ArticleDiffCallback(),
            binding = { layoutInflater, parent ->
                ArticleItemBinding.inflate(layoutInflater, parent, false)
            },
            bind = { article ->
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
        ViewModelProvider(this)
            .get<NewsListBySourceComponentViewModel>()
            .newsListBySourceComponent
            .inject(this)
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        newsListBySourceViewModel.onSourceIdChanged(requireArguments().getString(SOURCE_ID_KEY))
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

        newsAdapter.addOnLoadMoreListener(newsListBySourceViewModel::onLoadNextPage)

        newsListBySourceSearchRecyclerView.layoutManager = LinearLayoutManager(context)
        newsListBySourceSearchRecyclerView.adapter = newsSearchAdapter

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
            searchItem.setOnActionExpandListener(
                object : MenuItem.OnActionExpandListener {
                    override fun onMenuItemActionExpand(p0: MenuItem): Boolean {
                        searchDisposable = newsListBySourceViewModel
                            .searchPagerObservable
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(newsSearchAdapter::submitPage)

                        with(binding) {
                            newsListBySourceSwipeRefreshLayout.isVisible = false
                            newsListBySourceSearchRecyclerView.isVisible = true
                        }
                        return true
                    }

                    override fun onMenuItemActionCollapse(p0: MenuItem): Boolean {
                        with(binding) {
                            newsListBySourceSwipeRefreshLayout.isVisible = true
                            newsListBySourceSearchRecyclerView.isVisible = false
                        }
                        searchDisposable?.dispose()
                        return true
                    }
                }
            )
        }

        override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
            if (menuItem.itemId == android.R.id.home) {
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }
            return true
        }
    }

    private fun setupSwipeRefresh() = with(binding) {
        newsListBySourceSwipeRefreshLayout.setOnRefreshListener {
            newsListBySourceViewModel.refreshPager()
        }
    }

    private fun setupNewsPagerObserver() = with(binding) {
        val disposable = newsListBySourceViewModel.newsPagerObservable
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

}