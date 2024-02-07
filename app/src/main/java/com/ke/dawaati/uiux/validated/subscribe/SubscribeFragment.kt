package com.ke.dawaati.uiux.validated.subscribe

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.ke.dawaati.R
import com.ke.dawaati.databinding.SubscribeFragmentBinding
import com.ke.dawaati.db.model.SubscriptionTypesEntity
import com.ke.dawaati.util.dialogs.loader
import com.ke.dawaati.util.viewBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class SubscribeFragment : Fragment(R.layout.subscribe_fragment) {

    private val viewModel: SubscribeViewModel by viewModel()

    private val binding by viewBinding(SubscribeFragmentBinding::bind)

    private var dialog: Dialog? = null
    private var subscriptionOption: SubscriptionTypesEntity? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        setUpObservers()
        setupClickListeners()
        setupInputObservers()
        setupListAdapter()
    }

    private fun setupToolbar() {
        binding.apply {
            toolbar.setNavigationOnClickListener { findNavController().navigateUp() }
        }
    }

    private fun setupInputObservers() {
        binding.apply {
            subscriptionNumberInput.editText?.doAfterTextChanged {
                enableSubscribeAction()
            }
        }
    }

    private fun setupClickListeners() {
        binding.apply {
            subscribeAction.setOnClickListener {
                viewModel.subscribeToDawati(
                    phoneNumber = subscriptionNumberInput.editText?.text?.trim().toString(),
                    subscriptionType = subscriptionOption?.title ?: ""
                )
            }
        }
    }

    private fun setUpObservers() {
        viewModel.uiState.observe(viewLifecycleOwner) {
            when (it) {
                SubscribeUIState.Loading -> renderLoading()
                is SubscribeUIState.Success -> {
                    dialog?.dismiss()
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                }
                is SubscribeUIState.SubscriptionOptions -> {
                    renderSubscriptionOptions(subscriptionItems = it.subscriptionItems)
                    binding.subscriptionNumberInput.editText?.setText(it.phoneNumber)
                }
                is SubscribeUIState.Error -> {
                    dialog?.dismiss()
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun renderSubscriptionOptions(subscriptionItems: List<SubscriptionTypesEntity>) {
        subscriptionAdapter.submitList(subscriptionItems)
    }

    private fun renderLoading() {
        dialog = Dialog(requireContext())
        dialog?.loader()
    }

    private fun setupListAdapter() {
        binding.apply {
            subscriptionOptions.adapter = subscriptionAdapter
        }
    }

    private val subscriptionAdapter: SubscribeAdapter by lazy {
        SubscribeAdapter(
            selectedOption = {
                subscriptionOption = it
                enableSubscribeAction()
            }
        )
    }

    private fun enableSubscribeAction() {
        binding.apply {
            subscribeAction.isEnabled = subscriptionOption != null && subscriptionNumberInput.editText?.text?.trim().toString()
                .isNotEmpty()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        dialog?.dismiss()
    }
}
