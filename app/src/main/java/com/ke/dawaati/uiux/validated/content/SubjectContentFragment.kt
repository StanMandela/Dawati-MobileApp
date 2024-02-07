package com.ke.dawaati.uiux.validated.content

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.ke.dawaati.R
import com.ke.dawaati.databinding.SubjectContentFragmentBinding
import com.ke.dawaati.util.viewBinding

class SubjectContentFragment : Fragment(R.layout.subject_content_fragment) {

    private val binding by viewBinding(SubjectContentFragmentBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        setupBottomNavigation()
    }

    private fun setupToolbar() {
        binding.apply {
            toolbar.setNavigationOnClickListener { findNavController().navigateUp() }
        }
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.apply {
            setupWithNavController(requireActivity().findNavController(R.id.contentHostFragment))

            requireActivity().findNavController(R.id.contentHostFragment).addOnDestinationChangedListener { _, _, args ->
                // Top level items should have such argument with value set to true
                isVisible = true
            }
        }
    }
}
