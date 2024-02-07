package com.ke.dawaati.uiux.authenticate.activation

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.ke.dawaati.R
import com.ke.dawaati.databinding.SignActivationFragmentBinding
import com.ke.dawaati.uiux.authenticate.AuthenticationUIState
import com.ke.dawaati.uiux.authenticate.AuthenticationViewModel
import com.ke.dawaati.util.dialogs.loader
import com.ke.dawaati.util.setupAppVersion
import com.ke.dawaati.util.viewBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class SignActivationFragment : Fragment(R.layout.sign_activation_fragment) {

    private val viewModel: AuthenticationViewModel by viewModel()

    private val binding by viewBinding(SignActivationFragmentBinding::bind)

    private val args: SignActivationFragmentArgs by navArgs()

    private var bottomSheet: BottomSheetDialogFragment? = null

    private var dialog: Dialog? = null
    private var loginState = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupClickListener()
        inputListener()
        setupObservers()
    }

    private fun setupClickListener() {
        binding.apply {
            otpMessage.text = "Welcome to Dawati ${args.userName ?: ""}. An OTP has been send to your phone number ${args.phoneNumber ?: args.emailAddress ?: ""}. Please enter it to activate your account"

            activateAction.setOnClickListener {
                viewModel.activateUser(
                    email = args.emailAddress ?: args.phoneNumber ?: "",
                    code = otpInput.editText!!.text.toString().trim(),
                    context = requireContext()
                )
            }

            appVersion.text = "Version ${setupAppVersion(activity = requireActivity())}"
        }
    }

    private fun inputListener() {
        binding.apply {
            otpInput.editText?.doAfterTextChanged {
                activateAction.isEnabled = !it.isNullOrEmpty()
            }
        }
    }

    private fun setupObservers() {
        viewModel.uiState.observe(viewLifecycleOwner) {
            when (it) {
                AuthenticationUIState.Loading ->
                    renderLoading()
                is AuthenticationUIState.Error -> {
                    dialog?.dismiss()
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                }
                is AuthenticationUIState.Success -> {
                    dialog?.dismiss()
                    if (args.fromSignIn)
                        findNavController().navigate(SignActivationFragmentDirections.toHomeFragment())
                    else
                        findNavController().navigate(SignActivationFragmentDirections.toSignInFragment())
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
