package com.skid.newsapp.ui.sources

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.skid.newsapp.R
import com.skid.newsapp.databinding.FragmentSourcesBinding

class SourcesFragment : Fragment() {

    private var _binding: FragmentSourcesBinding? = null
    private val binding get() = checkNotNull(_binding)


    private val sourcesAdapter by lazy {
        SourcesAdapter { sourceItem ->
            // TODO(navigate to articles from source)
        }
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
        setHasOptionsMenu(true)
        (requireActivity() as AppCompatActivity).supportActionBar?.title =
            getString(R.string.sources)

        setupRecyclerView()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupRecyclerView() = with(binding) {
        sourcesRecyclerView.layoutManager = LinearLayoutManager(context)
        sourcesRecyclerView.adapter = sourcesAdapter
        sourcesRecyclerView.addItemDecoration(DividerItemDecoration(context, RecyclerView.VERTICAL))
    }

}