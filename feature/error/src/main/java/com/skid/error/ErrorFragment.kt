package com.skid.error

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import com.skid.error.databinding.FragmentErrorBinding
import com.skid.utils.Constants.ERROR_MESSAGE_KEY
import com.skid.utils.Constants.ERROR_RESULT_KEY

class ErrorFragment : Fragment() {

    private var _binding: FragmentErrorBinding? = null
    private val binding get() = checkNotNull(_binding)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentErrorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        parentFragmentManager.setFragmentResult(ERROR_RESULT_KEY, bundleOf())
        setupErrorMessage()
        setupRefreshButton()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupErrorMessage() {
        binding.errorMessage.text = requireArguments().getString(ERROR_MESSAGE_KEY)
    }

    private fun setupRefreshButton() = with(binding) {
        errorRefreshButton.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

}