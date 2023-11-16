package com.skid.saved

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
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.skid.saved.databinding.FragmentSavedBinding
import com.skid.saved.di.SavedComponentViewModel
import com.skid.ui.createAdapter
import com.skid.ui.databinding.ArticleItemBinding
import com.skid.utils.collectFlow
import javax.inject.Inject
import javax.inject.Provider

class SavedFragment : Fragment() {

    private var _binding: FragmentSavedBinding? = null
    private val binding get() = checkNotNull(_binding)

    @Inject
    lateinit var viewModelProvider: Provider<SavedViewModel.Factory>
    private val savedViewModel: SavedViewModel by viewModels { viewModelProvider.get() }

    @Inject
    lateinit var router: SavedRouter

    private val savedArticlesAdapter by lazy { createArticlesAdapter() }

    override fun onAttach(context: Context) {
        ViewModelProvider(this)
            .get<SavedComponentViewModel>()
            .savedComponent
            .inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentSavedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        setupRecyclerView()
        setupSavedArticlesCollect()
        setupSwipeRefreshLayout()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupRecyclerView() = with(binding) {
        savedRecyclerView.layoutManager = LinearLayoutManager(context)
        savedRecyclerView.adapter = savedArticlesAdapter
        savedRecyclerView.addItemDecoration(DividerItemDecoration(context, RecyclerView.VERTICAL))
    }

    private fun createArticlesAdapter() = createAdapter(
        binding = { inflater, parent -> ArticleItemBinding.inflate(inflater, parent, false) },
        bind = { article ->
            root.setOnClickListener { router.onArticleProfile(article) }
            articleItemImage.load(article.imageUrl) {
                crossfade(true)
            }
            articleItemSourceImage.setImageResource(article.sourceDrawableId)
            articleItemSourceName.text = article.sourceName
            articleItemTitle.text = article.title
        },
        diffCallback = ArticleDiffCallback()
    )

    private fun setupSavedArticlesCollect() = with(binding) {
        collectFlow(savedViewModel.savedArticles) { articles ->
            savedArticlesAdapter.submitList(articles)

            savedSwipeRefreshLayout.isVisible = true
            savedProgressBar.isVisible = false
        }
    }

    private fun setupSwipeRefreshLayout() = with(binding) {
        savedSwipeRefreshLayout.setOnRefreshListener {
            savedViewModel.onRefreshChanged(true)
        }

        collectFlow(savedViewModel.refresh) { refresh ->
            savedSwipeRefreshLayout.isRefreshing = refresh
        }
    }

    private fun setupToolbar() {
        (requireActivity() as AppCompatActivity).supportActionBar?.title =
            getString(com.skid.ui.R.string.saved)
    }

}