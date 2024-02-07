package com.ke.dawaati.uiux.validated.career

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.ke.dawaati.R
import com.ke.dawaati.databinding.CareersFragmentBinding
import com.ke.dawaati.util.viewBinding

class CareerFragment : Fragment(R.layout.careers_fragment) {

    private val binding by viewBinding(CareersFragmentBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
    }

    private fun setupToolbar() {
        binding.apply {
            toolbar.setNavigationOnClickListener { findNavController().navigateUp() }
        }
    }
}
