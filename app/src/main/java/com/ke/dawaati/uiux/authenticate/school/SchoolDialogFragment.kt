package com.ke.dawaati.uiux.authenticate.school

import android.app.Dialog
import android.content.res.Resources
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.ke.dawaati.R
import com.ke.dawaati.databinding.SchoolDialogFragmentBinding
import com.ke.dawaati.db.model.SchoolEntity
import com.ke.dawaati.uiux.authenticate.AuthenticationUIState
import com.ke.dawaati.uiux.authenticate.AuthenticationViewModel
import com.ke.dawaati.util.viewBinding
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import org.koin.androidx.viewmodel.ext.android.viewModel

@ExperimentalCoroutinesApi
@FlowPreview
class SchoolDialogFragment(
    val selectedVehicleCallBack: (SchoolEntity) -> (Unit)
) : BottomSheetDialogFragment() {

    private val viewModel: AuthenticationViewModel by viewModel()

    private val binding by viewBinding(SchoolDialogFragmentBinding::bind)

    private var selectedSchool: SchoolEntity? = null

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
    ) = inflater.inflate(R.layout.school_dialog_fragment, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        setupInputListener()
        setupObserver()
        setupListAdapter()

        viewModel.loadSchools()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener { dismiss() }
    }

    private fun setupInputListener() {
        binding.apply {
            search.editText?.let {
                it.doAfterTextChanged { editable ->
                    schoolAdapter.filter.filter(editable ?: "")
                }
            }
        }
    }

    private fun setupObserver() {
        binding.apply {
            viewModel.uiState.observe(viewLifecycleOwner) {
                when (it) {
                    AuthenticationUIState.Loading -> {
                        emptyGroup.isGone = true
                        loadingGroup.isVisible = true
                        schoolsList.isGone = true
                    }
                    is AuthenticationUIState.ListOfSchools ->
                        renderSchools(schools = it.schoolsList)
                }
            }
        }
    }

    private fun setupListAdapter() {
        binding.apply {
            schoolsList.adapter = schoolAdapter
        }
    }

    private fun renderSchools(schools: List<SchoolEntity>) = with(binding) {
        schoolAdapter.submitList(schools = schools)
        emptyGroup.isVisible = schools.isEmpty()
        schoolsList.isVisible = schools.isNotEmpty()
        loadingGroup.isGone = true
    }

    private val schoolAdapter: SchoolAdapter by lazy {
        SchoolAdapter(
            schoolSelected = {
                selectedSchool = it
                selectedVehicleCallBack(selectedSchool!!)
                dismiss()
            }
        )
    }
}
