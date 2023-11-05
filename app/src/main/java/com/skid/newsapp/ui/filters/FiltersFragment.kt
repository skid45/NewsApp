package com.skid.newsapp.ui.filters

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.children
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip
import com.skid.newsapp.R
import com.skid.newsapp.databinding.FragmentFiltersBinding

class FiltersFragment : Fragment() {

    private var _binding: FragmentFiltersBinding? = null
    private val binding get() = checkNotNull(_binding)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentFiltersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToggleButton()
        setupChipsGroup()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupToggleButton() = with(binding) {
        filtersButtonGroup.addOnButtonCheckedListener { group, _, _ ->
            group.children.forEach { button ->
                button as MaterialButton
                button.icon = if (button.isChecked) {
                        AppCompatResources.getDrawable(requireContext(), R.drawable.ic_check)
                } else null
            }
        }
        filtersButtonGroup.check(R.id.filters_popular_button)
    }

    private fun setupChipsGroup() = with(binding) {
        filtersChipsGroup.setOnCheckedStateChangeListener { group, _ ->
            val checkedChipsTitles = group.children
                .filter { (it as Chip).isChecked }
                .map { (it as Chip).text.toString() }
                .toList()
        }
    }
}