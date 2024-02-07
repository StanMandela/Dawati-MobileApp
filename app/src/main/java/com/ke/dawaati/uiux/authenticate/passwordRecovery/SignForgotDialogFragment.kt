package com.ke.dawaati.uiux.authenticate.passwordRecovery

import android.app.Dialog
import android.content.res.Resources
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.widget.doAfterTextChanged
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.ke.dawaati.R
import com.ke.dawaati.databinding.SignForgotDialogFragmentBinding
import com.ke.dawaati.uiux.authenticate.AuthenticationUIState
import com.ke.dawaati.uiux.authenticate.AuthenticationViewModel
import com.ke.dawaati.util.dialogs.loader
import com.ke.dawaati.util.dialogs.success
import com.ke.dawaati.util.toggleState
import com.ke.dawaati.util.viewBinding
import kotlinx.android.synthetic.main.dialog_loader.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class SignForgotDialogFragment : BottomSheetDialogFragment() {

    private val viewModel: AuthenticationViewModel by viewModel()

    private val binding by viewBinding(SignForgotDialogFragmentBinding::bind)

    private var loadingDialog: Dialog? = null

    /**
     * This function makes BottomSheetDialogFragment full screen and without collapsed state
     * For some reason this doesn't work without the params.height
     */
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setCanceledOnTouchOutside(false)
        dialog.setOnShowListener {
            val bottomSheet = dialog.findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)
            val params = bottomSheet.layoutParams
            val height: Int = Resources.getSystem().displayMetrics.heightPixels
            val maxHeight = (height * 0.90).toInt()
            params.height = maxHeight
            bottomSheet.layoutParams = params
            BottomSheetBehavior.from(bottomSheet).apply {
                state = BottomSheetBehavior.STATE_EXPANDED
                skipCollapsed = true
            }
        }
        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = inflater.inflate(R.layout.sign_forgot_dialog_fragment, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        setupClickListener()
        inputListener()
        setupObservers()

        binding.apply {
            resetAction.toggleState(
                context = requireContext(),
                enabled = false
            )
        }
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener { dismiss() }
    }

    private fun setupClickListener() {
        binding.apply {
            resetAction.setOnClickListener {
                viewModel.resetPassword(
                    emailAddress = emailInput.editText?.text.toString().trim()
                )
            }
        }
    }

    private fun inputListener() {
        binding.apply {
            emailInput.editText?.doAfterTextChanged {
                resetAction.toggleState(
                    context = requireContext(),
                    enabled = !it.isNullOrEmpty()
                )
            }
        }
    }

    private fun setupObservers() {
        viewModel.uiState.observe(viewLifecycleOwner) {
            when (it) {
                AuthenticationUIState.Loading ->
                    renderLoading()
                is AuthenticationUIState.Error -> {
                    loadingDialog?.dismiss()
                    binding.emailInput.editText?.error = it.message
                }
                is AuthenticationUIState.Success -> {
                    loadingDialog?.dismiss()
                    loadingDialog?.success(
                        message = it.message,
                        dismissCallBack = {
                            dismiss()
                        }
                    )
                }
            }
        }
    }

    private fun renderLoading() {
        loadingDialog = Dialog(requireContext())
        loadingDialog?.loader()
    }

    override fun onResume() {
        super.onResume()
        loadingDialog?.dismiss()
    }
}
