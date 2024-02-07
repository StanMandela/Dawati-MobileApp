package com.ke.dawaati.uiux.authenticate

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.ke.dawaati.R
import com.ke.dawaati.databinding.SignInFragmentBinding
import com.ke.dawaati.util.dialogs.loader
import com.ke.dawaati.util.observeEvent
import com.ke.dawaati.util.setupAppVersion
import com.ke.dawaati.util.viewBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class SignInFragment : Fragment(R.layout.sign_in_fragment) {

    private val viewModel: AuthenticationViewModel by viewModel()

    private val binding by viewBinding(SignInFragmentBinding::bind)

    private var bottomSheet: BottomSheetDialogFragment? = null

    private var dialog: Dialog? = null
    private var loginState = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupClickListener()
        setupObservers()
    }

    private fun setupClickListener() {
        binding.apply {
            signInAction.setOnClickListener {
                viewModel.signIn(
                    email_phone = emailAddressInput.editText!!.text.toString().trim(),
                    password = passwordInput.editText!!.text.toString().trim(),
                    context = requireContext()
                )
            }
            signInForgot.setOnClickListener {
                viewModel.navigateToPasswordReset()
            }
            signUpAction.setOnClickListener {
                findNavController().navigate(SignInFragmentDirections.toSignUpFragment())
            }

            appVersion.text = "Version ${setupAppVersion(activity = requireActivity())}"
        }
    }

    private fun setupObservers() {
        viewModel.uiState.observe(viewLifecycleOwner) {
            when (it) {
                AuthenticationUIState.Loading ->
                    renderLoading()
                is AuthenticationUIState.Error -> {
                    dialog?.dismiss()
                    binding.emailAddressInput.error = it.message
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
        viewModel.action.observeEvent(viewLifecycleOwner) {
            when (it) {
                is AuthenticationActions.Navigate -> {
                    dialog?.dismiss()
                    loginState = true
                    binding.apply {
                        emailAddressInput.editText?.setText("")
                        passwordInput.editText?.setText("")
                    }
                    findNavController().navigate(it.destination)
                }
                is AuthenticationActions.BottomNavigate -> {
                    bottomSheet = it.bottomSheet
                    bottomSheet?.let { bsdf ->
                        bsdf.show(parentFragmentManager, bsdf.tag)
                    }
                }
            }
        }
    }

    private fun renderLoading() {
        dialog = Dialog(requireContext())
        dialog?.loader()
    }

    override fun onResume() {
        super.onResume()
        if (loginState) {
            dialog?.dismiss()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        dialog?.dismiss()
        bottomSheet?.dismiss()
    }
}
