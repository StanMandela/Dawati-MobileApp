package com.ke.dawaati.uiux.validated.content.evaluations

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.ke.dawaati.R
import com.ke.dawaati.databinding.SubjectContentEvaluationsFragmentBinding
import com.ke.dawaati.preference.ConfigurationPrefs
import com.ke.dawaati.util.observeEvent
import com.ke.dawaati.util.viewBinding
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class ContentEvaluationFragment : Fragment(R.layout.subject_content_evaluations_fragment) {

    private val viewModel: ContentEvaluationViewModel by viewModel()

    private val binding by viewBinding(SubjectContentEvaluationsFragmentBinding::bind)

    private val prefs: ConfigurationPrefs by inject()

    private val mainNavController: NavController by lazy { requireActivity().findNavController(R.id.navHostFragment) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        setupObservers()
        setupListAdapter()
        setOnClickListener()

        viewModel.loadAllExam()
    }

    private fun setupToolbar() {
        binding.apply {
            title.text = "Evaluations, ${prefs.getLevel().second}"
        }
    }

    private fun setupObservers() {
        viewModel.uiState.observe(viewLifecycleOwner) {
            when (it) {
                ContentEvaluationUIState.Loading -> renderLoader()
                is ContentEvaluationUIState.Evaluations ->
                    renderEvaluations(evaluationsEntity = it.evaluations)
                is ContentEvaluationUIState.Error -> {
                    Toast.makeText(requireContext(), "${it.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }

        viewModel.action.observeEvent(viewLifecycleOwner) {
            when (it) {
                is ContentEvaluationActions.Navigate -> mainNavController.navigate(it.destination)
            }
        }
    }

    private fun setupListAdapter() {
        binding.apply {
            evaluationList.adapter = contentEvaluationsAdapter
        }
    }

    private fun setOnClickListener() {
        binding.apply {
            previousAttempts.setOnClickListener {
                viewModel.viewPreviousAttempts()
            }
        }
    }

    private fun renderLoader() {
        binding.apply {
            evaluationList.isGone = true
            evaluationEmptyState.apply {
                isGone = true
                setEmptyTitle(title = "No evaluations")
                setEmptySubTitle(subTitle = "There are no evaluations for ${prefs.getSubject().second}, ${prefs.getLevel().second}")
            }
            evaluationLoadingLargeState.apply {
                isVisible = true
                setLoadingTitle("Loading evaluations for ${prefs.getSubject().second}, ${prefs.getLevel().second}")
            }
        }
    }

    private fun renderEvaluations(evaluationsEntity: List<DisplayEvaluations>) {
        binding.apply {
            evaluationList.isVisible = evaluationsEntity.isNotEmpty()
            evaluationEmptyState.isVisible = evaluationsEntity.isEmpty()
            evaluationLoadingLargeState.isGone = true
            contentEvaluationsAdapter.setEvaluations(evaluationsEntity = evaluationsEntity)
        }
    }

    private val contentEvaluationsAdapter: ContentEvaluationsAdapter by lazy {
        ContentEvaluationsAdapter(
            selectedEvaluation = { displayEvaluations ->
                viewModel.viewExam(displayEvaluations = displayEvaluations)
            }
        )
    }
}
