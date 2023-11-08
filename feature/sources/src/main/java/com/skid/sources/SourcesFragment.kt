package com.skid.sources

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
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.skid.sources.databinding.FragmentSourcesBinding
import com.skid.sources.di.SourcesComponentViewModel
import com.skid.utils.collectFlow
import javax.inject.Inject
import javax.inject.Provider

class SourcesFragment : Fragment() {

    private var _binding: FragmentSourcesBinding? = null
    private val binding get() = checkNotNull(_binding)

    @Inject
    lateinit var viewModelProvider: Provider<SourcesViewModel.Factory>
    private val sourcesViewModel: SourcesViewModel by activityViewModels { viewModelProvider.get() }

    private val sourcesAdapter by lazy {
        SourcesAdapter { sourceItem ->
            // TODO(navigate to articles from source)
        }
    }

    private val sourcesByQueryAdapter by lazy {
        SourcesAdapter { sourceItem ->
            // TODO(navigate to articles from source)
        }
    }

    override fun onAttach(context: Context) {
        ViewModelProvider(this)
            .get<SourcesComponentViewModel>()
            .sourcesComponent
            .inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentSourcesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        setupRecyclerView()
        setupSwipeToRefresh()
        collectUiState()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupRecyclerView() = with(binding) {
        sourcesRecyclerView.layoutManager = LinearLayoutManager(context)
        sourcesRecyclerView.adapter = sourcesAdapter
        sourcesRecyclerView.addItemDecoration(DividerItemDecoration(context, RecyclerView.VERTICAL))

        sourcesSearchRecyclerView.layoutManager = LinearLayoutManager(context)
        sourcesSearchRecyclerView.adapter = sourcesByQueryAdapter
        sourcesSearchRecyclerView.addItemDecoration(
            DividerItemDecoration(context, RecyclerView.VERTICAL)
        )
    }

    private fun setupToolbar() {
        (requireActivity() as AppCompatActivity).supportActionBar?.title =
            getString(com.skid.ui.R.string.sources)

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
                            sourcesViewModel.onEvent(SourcesEvent.OnSearchByQuery(newText))
                        }
                        return true
                    }
                }
            )
            searchItem.setOnActionExpandListener(
                object : MenuItem.OnActionExpandListener {
                    override fun onMenuItemActionExpand(p0: MenuItem): Boolean = true

                    override fun onMenuItemActionCollapse(p0: MenuItem): Boolean {
                        sourcesViewModel.onEvent(SourcesEvent.OnUpdateSources)
                        return true
                    }
                }
            )
        }

        override fun onMenuItemSelected(menuItem: MenuItem): Boolean = true
    }

    private fun collectUiState() = with(binding) {
        collectFlow(sourcesViewModel.uiState) { uiState ->
            when (uiState) {
                SourcesUiState.Refresh -> {
                    sourcesSwipeRefreshLayout.isRefreshing = true
                    sourcesProgressBar.isVisible = false
                }

                SourcesUiState.Loading -> {
                    sourcesSearchRecyclerView.isVisible = false
                    sourcesSwipeRefreshLayout.isVisible = false
                    sourcesProgressBar.isVisible = true
                }

                is SourcesUiState.Success -> {
                    sourcesAdapter.submitList(uiState.sources)
                    sourcesSearchRecyclerView.isVisible = false
                    sourcesSwipeRefreshLayout.isVisible = true
                    sourcesProgressBar.isVisible = false
                    sourcesSwipeRefreshLayout.isRefreshing = false
                }

                is SourcesUiState.Search -> {
                    sourcesByQueryAdapter.submitList(uiState.sourcesByQuery)
                    sourcesSearchRecyclerView.isVisible = true
                    sourcesSwipeRefreshLayout.isVisible = false
                    sourcesProgressBar.isVisible = false
                }

                is SourcesUiState.Error -> {} // TODO(Navigate to ErrorFragment)
            }
        }
    }

    private fun setupSwipeToRefresh() = with(binding) {
        sourcesSwipeRefreshLayout.setOnRefreshListener {
            sourcesViewModel.onEvent(SourcesEvent.OnUpdateSources)
        }
    }

}