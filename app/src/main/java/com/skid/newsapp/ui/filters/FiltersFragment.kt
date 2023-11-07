package com.skid.newsapp.ui.filters

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip
import com.google.android.material.datepicker.MaterialDatePicker
import com.skid.newsapp.R
import com.skid.newsapp.appComponent
import com.skid.newsapp.databinding.FragmentFiltersBinding
import com.skid.newsapp.domain.model.Language
import com.skid.newsapp.domain.model.Sorting
import com.skid.newsapp.utils.getDisplayChosenRange
import com.skid.newsapp.utils.resolveAttributeColor
import java.util.Calendar
import java.util.TimeZone
import javax.inject.Inject
import javax.inject.Provider

class FiltersFragment : Fragment() {

    private var _binding: FragmentFiltersBinding? = null
    private val binding get() = checkNotNull(_binding)

    @Inject
    lateinit var viewModelProvider: Provider<FiltersViewModel.Factory>
    private val filtersViewModel: FiltersViewModel by viewModels { viewModelProvider.get() }

    override fun onCreate(savedInstanceState: Bundle?) {
        requireActivity().appComponent.inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentFiltersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        setupToggleButton()
        setupChipsGroup()
        setupDatePicker()
        setupUiStateObserver()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupToolbar() {
        (requireActivity() as AppCompatActivity).supportActionBar?.apply {
            title = getString(R.string.filters)
            setDisplayHomeAsUpEnabled(true)
        }

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(menuProvider, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private val menuProvider = object : MenuProvider {
        override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
            menu.clear()
            menuInflater.inflate(R.menu.filters_menu, menu)
        }

        override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
            when (menuItem.itemId) {
                R.id.filters_menu_done -> {
                    filtersViewModel.onEvent(FiltersEvent.SaveFilters)
                    requireActivity().onBackPressedDispatcher.onBackPressed()
                }

                else -> return false
            }
            return true
        }
    }

    private fun setupToggleButton() = with(binding) {
        filtersButtonGroup.addOnButtonCheckedListener { group, _, _ ->
            group.children.forEach { button ->
                button as MaterialButton
                button.icon = if (button.isChecked) {
                    val sortBy = Sorting.valueOf(button.text.toString().uppercase())
                    filtersViewModel.onEvent(FiltersEvent.OnSortByChanged(sortBy))
                    AppCompatResources.getDrawable(requireContext(), R.drawable.ic_check)
                } else null
            }
        }
    }

    private fun setupChipsGroup() = with(binding) {
        filtersChipsGroup.setOnCheckedStateChangeListener { group, _ ->
            val selectedLanguages = group.children
                .filter { (it as Chip).isChecked }
                .map { chip ->
                    chip as Chip
                    Language.values().first { language ->
                        language.title == chip.text.toString()
                    }
                }
                .toList()

            filtersViewModel.onEvent(FiltersEvent.OnLanguagesChanged(selectedLanguages))
        }
    }

    private fun setupDatePicker() = with(binding) {
        val datePicker = MaterialDatePicker.Builder.dateRangePicker()
            .setTitleText("Select date")
            .setTheme(com.google.android.material.R.style.ThemeOverlay_Material3_MaterialCalendar)
            .build()

        filtersChooseDateButton.setOnClickListener {
            datePicker.show(childFragmentManager, null)
        }

        datePicker.addOnPositiveButtonClickListener { selection ->
            val startCalendar = Calendar.getInstance(TimeZone.getDefault()).apply {
                timeInMillis = selection.first
            }
            val endCalendar = Calendar.getInstance(TimeZone.getDefault()).apply {
                timeInMillis = selection.second
            }
            filtersViewModel.onEvent(FiltersEvent.OnChosenDatesChanged(startCalendar to endCalendar))
        }
        datePicker.addOnNegativeButtonClickListener {
            filtersViewModel.onEvent(FiltersEvent.OnChosenDatesChanged(null))
        }
    }

    private fun setupUiStateObserver() = with(binding) {
        filtersViewModel.uiState.observe(viewLifecycleOwner) { uiState ->
            val datePickerButton = (filtersChooseDateButton as MaterialButton)
            if (uiState.chosenDates == null) {
                val onSurfaceVariantColor = requireContext()
                    .resolveAttributeColor(com.google.android.material.R.attr.colorOnSurfaceVariant)
                val outlineColor = requireContext()
                    .resolveAttributeColor(com.google.android.material.R.attr.colorOutline)
                datePickerButton.setBackgroundColor(Color.TRANSPARENT)
                datePickerButton.iconTint = ColorStateList.valueOf(onSurfaceVariantColor)
                filtersChooseDateTextView.text = getString(R.string.choose_date)
                filtersChooseDateTextView.setTextColor(ColorStateList.valueOf(outlineColor))
            } else {
                val primaryColor = requireContext()
                    .resolveAttributeColor(com.google.android.material.R.attr.colorPrimary)
                val onPrimaryColor = requireContext()
                    .resolveAttributeColor(com.google.android.material.R.attr.colorOnPrimary)
                datePickerButton.setBackgroundColor(primaryColor)
                datePickerButton.iconTint = ColorStateList.valueOf(onPrimaryColor)
                filtersChooseDateTextView.text = uiState.chosenDates.getDisplayChosenRange()
                filtersChooseDateTextView.setTextColor(ColorStateList.valueOf(primaryColor))
            }

            uiState.languages.forEach { language ->
                filtersChipsGroup.children.forEach { chip ->
                    chip as Chip
                    if (chip.text.toString() == language.title) filtersChipsGroup.check(chip.id)
                }
            }

            filtersButtonGroup.children.forEach { button ->
                button as MaterialButton
                if (button.text.toString().lowercase() == uiState.sortBy.name.lowercase()) {
                    filtersButtonGroup.check(button.id)
                }
            }
        }
    }
}