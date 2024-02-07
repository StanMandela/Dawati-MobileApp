package com.ke.dawaati.uiux.validated.content.evaluations.reports

import android.os.Bundle
import android.view.View
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.ke.dawaati.R
import com.ke.dawaati.databinding.EvaluationsReportListFragmentBinding
import com.ke.dawaati.db.model.EvaluationsAttemptsEntity
import com.ke.dawaati.util.observeEvent
import com.ke.dawaati.util.viewBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class EvaluationsReportListFragment : Fragment(R.layout.evaluations_report_list_fragment) {

    private val viewModel: EvaluationsReportViewModel by viewModel()

    private val binding by viewBinding(EvaluationsReportListFragmentBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        setupObservers()
        setupListAdapter()

        viewModel.loadExamAttempts()
    }

    private fun setupToolbar() {
        binding.apply {
            toolbar.setNavigationOnClickListener { findNavController().navigateUp() }
        }
    }

    private fun setupObservers() {
        viewModel.uiState.observe(viewLifecycleOwner) {
            when (it) {
                EvaluationsReportUIState.Loading -> renderLoader()
                is EvaluationsReportUIState.AllAttempts ->
                    renderEvaluationAttempts(examAttempts = it.examAttempts)
                is EvaluationsReportUIState.Error -> {}
            }
        }

        viewModel.action.observeEvent(viewLifecycleOwner) {
            when (it) {
                is EvaluationsReportActions.Navigate ->
                    findNavController().navigate(it.destination)
            }
        }
    }

    private fun renderLoader() {
        binding.apply {
            evaluationEmptyState.isGone = true
            evaluationLoadingLargeState.isVisible = true
        }
    }

    private fun renderEvaluationAttempts(examAttempts: List<EvaluationsAttemptsEntity>) {
        binding.apply {
            evaluationList.isVisible = examAttempts.isNotEmpty()
            evaluationEmptyState.isVisible = examAttempts.isEmpty()
            evaluationLoadingLargeState.isGone = true
            evaluationsReportAdapter.setEvaluations(evaluationsEntity = examAttempts)
        }
    }

    private fun setupListAdapter() {
        binding.apply {
            evaluationList.adapter = evaluationsReportAdapter
        }
    }

    private val evaluationsReportAdapter: EvaluationsReportAdapter by lazy {
        EvaluationsReportAdapter(
            selectedEvaluation = { evaluationsAttempts ->
                viewModel.viewPreviousAttemptDetails(
                    evaluationName = evaluationsAttempts.exam_name,
                    exam_id = evaluationsAttempts.exam_id,
                    response_id = evaluationsAttempts.response_id
                )
            }
        )
    }
}
