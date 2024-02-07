package com.ke.dawaati.uiux.validated.level

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.ke.dawaati.R
import com.ke.dawaati.databinding.DialogFormSelectBinding
import com.ke.dawaati.db.model.LevelsEntity
import com.ke.dawaati.util.viewBinding
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview

@ExperimentalCoroutinesApi
@FlowPreview
class LevelDialogFragment(
    val currentLevel: String,
    val allLevels: List<LevelsEntity>,
    val selectedLevelCallBack: (LevelsEntity) -> (Unit)
) : BottomSheetDialogFragment() {

    private val binding by viewBinding(DialogFormSelectBinding::bind)

    private var selectedSchool: LevelsEntity? = null

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
    ) = inflater.inflate(R.layout.dialog_form_select, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        setupListAdapter()

        renderSchools()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener { dismiss() }
    }

    private fun setupListAdapter() {
        binding.apply {
            levelList.adapter = levelAdapter
        }
    }

    private fun renderSchools() {
        levelAdapter.setLevel(levelEntity = allLevels, selectedLevel = currentLevel)
    }

    private val levelAdapter: LevelAdapter by lazy {
        LevelAdapter(
            levelSelected = {
                selectedSchool = it!!
                selectedLevelCallBack(selectedSchool!!)
                dismiss()
            }
        )
    }
}
