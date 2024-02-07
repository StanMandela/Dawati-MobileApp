package com.ke.dawaati.uiux.validated.content.evaluations.reports.details

import android.content.Context
import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.checkbox.MaterialCheckBox
import com.google.android.material.textfield.TextInputLayout
import com.ke.dawaati.R
import com.ke.dawaati.api.response.AttemptDetailsResult
import com.ke.dawaati.databinding.EvaluationsReportDetailsFragmentBinding
import com.ke.dawaati.uiux.validated.content.evaluations.reports.EvaluationsReportUIState
import com.ke.dawaati.uiux.validated.content.evaluations.reports.EvaluationsReportViewModel
import com.ke.dawaati.util.viewBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class EvaluationsReportDetailsFragment : Fragment(R.layout.evaluations_report_details_fragment) {

    private val viewModel: EvaluationsReportViewModel by viewModel()

    private val args: EvaluationsReportDetailsFragmentArgs by navArgs()

    private val binding by viewBinding(EvaluationsReportDetailsFragmentBinding::bind)

    private lateinit var evaluationReport: AttemptDetailsResult

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        setupObservers()

        viewModel.loadPreviousAttemptDetails(
            exam_id = args.examID,
            response_id = args.responseID
        )
    }

    private fun setupToolbar() {
        binding.apply {
            toolbar.setNavigationOnClickListener { findNavController().navigateUp() }
            toolbarTitle.text = args.evaluationName
        }
    }

    private fun setupObservers() {
        viewModel.uiState.observe(viewLifecycleOwner) {
            when (it) {
                EvaluationsReportUIState.Loading -> renderLoader()
                is EvaluationsReportUIState.Error -> Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                is EvaluationsReportUIState.EvaluationReport -> it.examAttempts?.let { renderEvaluationReport(attemptReport = it) }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun renderEvaluationReport(attemptReport: AttemptDetailsResult?) {
        this.evaluationReport = attemptReport!!
        var questionNumber = 0
        binding.apply {
            evaluationLoadingLargeState.isGone = true
            evaluationEmptyState.isGone = true

            val examQuestions = evaluationReport.questions
            val examCorrectAnswers = evaluationReport.correct_answer

            examQuestions.forEach { singleQuestion ->
                questionNumber++
                val inflater = requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                val rowView: View = inflater.inflate(R.layout.evaluations_exam_container, null)
                val questionTitle = rowView.findViewById<TextView>(R.id.questionTitle)
                val question = rowView.findViewById<TextView>(R.id.question)
                val choicesTitle = rowView.findViewById<TextView>(R.id.choicesTitle)
                // Question Type 1
                val radioBoolean = rowView.findViewById<RadioGroup>(R.id.radioBoolean)
                // Question Type 2
                val radioMultiple = rowView.findViewById<RadioGroup>(R.id.radioMultiple)
                // Question Type 3
                val multiChoiceList = rowView.findViewById<LinearLayout>(R.id.multiChoiceList)
                // Question Type 4
                val answerInput = rowView.findViewById<TextInputLayout>(R.id.answerInput)

                val instructions = rowView.findViewById<TextView>(R.id.instructions)

                questionTitle.text = "Question $questionNumber"
                question.text = singleQuestion.question
                choicesTitle.isVisible =
                    singleQuestion.type.equals("2", ignoreCase = true) ||
                    singleQuestion.type.equals("1", ignoreCase = true)

                val multiChoices = evaluationReport.answers.filter {
                    it.question_id.equals(
                        singleQuestion.question_id,
                        ignoreCase = true
                    )
                }

                when {
                    singleQuestion.type.equals("1", ignoreCase = true) -> {
                        radioBoolean.apply {
                            isVisible = true
                            orientation = LinearLayout.VERTICAL
                        }
                        instructions.text = "* Choose the correct option *"

                        val layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        )
                        layoutParams.setMargins(16.dp, 8.dp, 16.dp, 8.dp)

                        multiChoices.forEach { choice ->
                            val choiceButton = RadioButton(requireContext())
                            choiceButton.apply {
                                setTextAppearance(R.style.MainItemTextAppearance)
                                textSize = 16.toFloat()
                                width = maxWidth
                                id = choice.choice_id.toInt()
                                text = choice.choice.trim()
                                setPadding(0.dp, 4.dp, 0.dp, 4.dp)
                                if (choice.choice.trim().equals(examCorrectAnswers[questionNumber - 1], ignoreCase = true)) {
                                    setTextColor(resources.getColor(R.color.colorPositive, null))
                                    isChecked = true
                                }
                            }
                            radioBoolean.addView(choiceButton)
                        }
                    }
                    singleQuestion.type.equals("2", ignoreCase = true) -> {
                        radioMultiple.apply {
                            isVisible = true
                            orientation = LinearLayout.VERTICAL
                        }
                        instructions.text = "* Choose one answer *"

                        multiChoices.forEach { choice ->
                            val choiceButton = RadioButton(requireContext())
                            choiceButton.apply {
                                setTextAppearance(R.style.MainItemTextAppearance)
                                textSize = 16.toFloat()
                                width = maxWidth
                                id = choice.choice_id.toInt()
                                text = choice.choice.trim()
                                if (choice.choice.trim().equals(examCorrectAnswers[questionNumber - 1], ignoreCase = true)) {
                                    setTextColor(resources.getColor(R.color.colorPositive, null))
                                    isChecked = true
                                }
                            }
                            radioMultiple.addView(choiceButton)
                        }
                    }
                    singleQuestion.type.equals("3", ignoreCase = true) -> {
                        multiChoiceList.isVisible = true
                        multiChoiceList.removeAllViews()
                        instructions.text = "* Choose one or more choices *"

                        multiChoices.forEach { choice ->
                            val choiceInflater =
                                requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                            val multiView =
                                choiceInflater.inflate(R.layout.item_answer_picker, null)
                            val choiceID = multiView.findViewById<TextView>(R.id.choiceID)
                            val choiceName = multiView.findViewById<TextView>(R.id.choiceName)
                            val choiceStatus = multiView.findViewById<TextView>(R.id.choiceStatus)
                            val choiceRadio = multiView.findViewById<MaterialCheckBox>(R.id.choiceRadio)
                            val choiceParent = multiView.findViewById<ConstraintLayout>(R.id.choiceParent)

                            if (choice.choice.trim().equals(examCorrectAnswers[questionNumber - 1], ignoreCase = true)) {
                                choiceName.setTextColor(
                                    resources.getColor(
                                        R.color.colorPositive,
                                        null
                                    )
                                )
                                choiceRadio.isChecked = true
                            }

                            choiceID.text = choice.choice_id
                            choiceName.text = choice.choice.trim()
                            choiceStatus.text = choice.status

                            multiView.isClickable = true

                            multiChoiceList.addView(multiView, multiChoiceList.childCount)
                        }
                    }
                    else -> {
                        answerInput.isVisible = true
                        instructions.text = "* Input the correct answer *"
                    }
                }

                val layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                layoutParams.setMargins(16.dp, 8.dp, 16.dp, 8.dp)
                rowView.layoutParams = layoutParams

                evaluationQuestions.addView(rowView, evaluationQuestions.childCount)
            }
        }
    }

    private fun renderLoader() {
        binding.apply {
            evaluationEmptyState.isGone = true
            evaluationLoadingLargeState.isVisible = true
        }
    }

    private val Int.dp: Int
        get() = (this * Resources.getSystem().displayMetrics.density + 0.5f).toInt()
}
