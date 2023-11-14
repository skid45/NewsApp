package com.skid.newslistbysource

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
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
    }

    private fun setupToolbar() {
        (requireActivity() as AppCompatActivity).supportActionBar?.apply {
            title = requireArguments().getString(SOURCE_NAME_KEY)
            setDisplayHomeAsUpEnabled(true)
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
            .subscribe {
                newsAdapter.submitPage(it)
                newsListBySourceSwipeRefreshLayout.isRefreshing = false
                newsListBySourceProgressBar.isVisible = false
                newsListBySourceSwipeRefreshLayout.isVisible = true
            }

        disposables.add(disposable)
    }

}