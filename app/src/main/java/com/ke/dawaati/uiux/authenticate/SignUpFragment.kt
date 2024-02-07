package com.ke.dawaati.uiux.authenticate

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.ke.dawaati.R
import com.ke.dawaati.databinding.SignUpFragmentBinding
import com.ke.dawaati.db.model.LevelsEntity
import com.ke.dawaati.db.model.SchoolEntity
import com.ke.dawaati.util.dialogs.loader
import com.ke.dawaati.util.observeEvent
import com.ke.dawaati.util.viewBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class SignUpFragment : Fragment(R.layout.sign_up_fragment) {

    private val viewModel: AuthenticationViewModel by viewModel()

    private val binding by viewBinding(SignUpFragmentBinding::bind)

    private var bottomSheet: BottomSheetDialogFragment? = null

    private var dialog: Dialog? = null

    private var selectedSchool: SchoolEntity? = null
    private var levelsEntity: LevelsEntity? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        setupPickers()
        setupObservers()
        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.apply {
            signUpAction.setOnClickListener {
                viewModel.signUp(
                    full_name = fullName.editText!!.text.toString().trim(),
                    email = emailAddress.editText!!.text.toString().trim(),
                    phone = phoneNumber.editText!!.text.toString().trim(),
                    gender = genderSpinner.text.toString().trim(),
                    level = levelsEntity?.level_code ?: "",
                    school_name = selectedSchool?.schoolName ?: "",
                    password = password.editText!!.text.toString().trim(),
                    confirmPassword = confirmPassword.editText!!.text.toString().trim(),
                    context = requireContext()
                )
            }
        }
    }

    private fun setupToolbar() {
        binding.apply {
            toolbar.setNavigationOnClickListener { findNavController().navigateUp() }
        }
    }

    private fun setupPickers() {
        binding.apply {
            val list = resources.getStringArray(R.array.array_gender)
            val genderAdapter: ArrayAdapter<String> =
                ArrayAdapter<String>(
                    requireContext(),
                    android.R.layout.simple_dropdown_item_1line,
                    list
                )
            genderSpinner.setAdapter(genderAdapter)

            schoolOption.apply {
                root.setOnClickListener {
                    viewModel.navigateToSchoolSelection()
                }
            }

            schoolLevelOption.apply {
                root.setOnClickListener {
                    viewModel.navigateToSchoolLevelSelection()
                }
            }
        }
    }

    private fun setupObservers() = with(binding) {
        viewModel.uiState.observe(viewLifecycleOwner) {
            when (it) {
                AuthenticationUIState.Loading ->
                    renderLoading()
                is AuthenticationUIState.Error -> {
                    dialog?.dismiss()
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                }
                is AuthenticationUIState.SchoolChoice -> {
                    selectedSchool = it.school
                    schoolOption.pickerTitle.text = it.school.schoolName
                }
                is AuthenticationUIState.SchoolLevelChoice -> {
                    levelsEntity = it.level
                    schoolLevelOption.pickerTitle.text = it.level.level_name
                }
                is AuthenticationUIState.Success -> {
                    dialog?.dismiss()
                    findNavController().navigate(
                        SignUpFragmentDirections.toSignActivationFragment(
                            userName = fullName.editText!!.text.toString().trim(),
                            emailAddress = emailAddress.editText!!.text.toString().trim(),
                            phoneNumber = phoneNumber.editText!!.text.toString().trim(),
                            fromSignIn = false
                        )
                    )
                }
            }
        }

        viewModel.action.observeEvent(viewLifecycleOwner) {
            when (it) {
                is AuthenticationActions.Navigate -> findNavController().navigate(it.destination)
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
        dialog?.dismiss()
    }

    override fun onDestroy() {
        super.onDestroy()
        dialog?.dismiss()
        bottomSheet?.dismiss()
    }
}
