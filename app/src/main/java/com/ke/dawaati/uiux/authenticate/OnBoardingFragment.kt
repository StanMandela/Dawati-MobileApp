package com.ke.dawaati.uiux.authenticate

import android.os.Bundle
import android.view.View
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.ke.dawaati.R
import com.ke.dawaati.databinding.OnBoardingFragmentBinding
import com.ke.dawaati.util.viewBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class OnBoardingFragment : Fragment(R.layout.on_boarding_fragment) {

    private val binding by viewBinding(OnBoardingFragmentBinding::bind)
    private val viewModel: AuthenticationViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupClickListener()
        setupObserver()

        viewModel.loadSchools()
    }

    private fun setupClickListener() {
        binding.apply {
            signInAction.setOnClickListener {
                findNavController().navigate(OnBoardingFragmentDirections.toSignInFragment())
            }
            signUpAction.setOnClickListener {
                findNavController().navigate(OnBoardingFragmentDirections.toSignUpFragment())
            }
        }
    }

    private fun setupObserver() {
        binding.apply {
            viewModel.uiState.observe(viewLifecycleOwner) {
                when (it) {
                    AuthenticationUIState.Loading -> {
                        actionsGroup.isGone = true
                        loadingGroup.isVisible = true
                    }
                    is AuthenticationUIState.ListOfSchools -> {
                        actionsGroup.isVisible = true
                        loadingGroup.isGone = true
                    }
                }
            }
        }
    }
}
