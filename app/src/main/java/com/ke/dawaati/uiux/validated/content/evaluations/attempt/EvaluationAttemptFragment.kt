package com.ke.dawaati.uiux.validated.content.evaluations.attempt

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.google.android.material.checkbox.MaterialCheckBox
import com.google.android.material.textfield.TextInputLayout
import com.ke.dawaati.R
import com.ke.dawaati.api.request.ExamAnswers
import com.ke.dawaati.api.response.EvaluationQuestionsResult
import com.ke.dawaati.databinding.EvaluationsAttemptFragmentBinding
import com.ke.dawaati.util.dp
import com.ke.dawaati.util.observeEvent
import com.ke.dawaati.util.viewBinding
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import org.koin.androidx.viewmodel.ext.android.viewModel

class EvaluationAttemptFragment : Fragment(R.layout.evaluations_attempt_fragment) {

    private val viewModel: EvaluationAttemptViewModel by viewModel()

    private val binding by viewBinding(EvaluationsAttemptFragmentBinding::bind)

    private val args: EvaluationAttemptFragmentArgs by navArgs()

    private var examQuestions: EvaluationQuestionsResult? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        setupObservers()
        setOnClickListener()

        viewModel.fetchAllExams()
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
                EvaluationAttemptUIState.Loading ->
                    renderLoader()
                EvaluationAttemptUIState.SubmitLoading ->
                    renderBannerLoader()
                is EvaluationAttemptUIState.Questions ->
                    renderQuestions(examQuestionsResult = it.examQuestions)
                is EvaluationAttemptUIState.Error -> {
                    Toast.makeText(requireContext(), "${it.message}", Toast.LENGTH_SHORT).show()
                }
                is EvaluationAttemptUIState.EvaluationResponse ->
                    renderBannerLoader(state = false)
            }
        }

        viewModel.action.observeEvent(viewLifecycleOwner) { action ->
            when (action) {
                is EvaluationAttemptActions.Navigate ->
                    findNavController().navigate(action.destination)
            }
        }
    }

    private fun renderQuestions(examQuestionsResult: EvaluationQuestionsResult?) {
        examQuestions = examQuestionsResult
        var questionNumber = 0
        binding.apply {
            examQuestions?.let {
                evaluationLoadingLargeState.isGone = true
                evaluationEmptyState.isGone = true

                val examQuestions = it.exam_questions

                examQuestions.forEach { singleQuestion ->
                    questionNumber++
                    val inflater = requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                    val rowView: View = inflater.inflate(R.layout.evaluations_exam_container, null)
                    val questionTitle = rowView.findViewById<TextView>(R.id.questionTitle)
                    val question = rowView.findViewById<TextView>(R.id.question)
                    val questionImage = rowView.findViewById<ImageView>(R.id.questionImage)
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
                    if (singleQuestion.attachment.isNotEmpty()) {
                        Glide
                            .with(requireContext())
                            .load("https://app.dawati.co.ke/questions_attachments/" + singleQuestion.attachment)
                            .into(questionImage)
                        questionImage.isVisible = true
                    }
                    choicesTitle.isVisible =
                        singleQuestion.type.equals("2", ignoreCase = true) ||
                        singleQuestion.type.equals("1", ignoreCase = true)

                    val multiChoices = it.exam_choices.filter {
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
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                        setTextAppearance(R.style.MainItemTextAppearance)
                                    }
                                    textSize = 16.toFloat()
                                    width = maxWidth
                                    id = choice.choice_id.toInt()
                                    text = choice.choice.trim()
                                    setPadding(0.dp, 4.dp, 0.dp, 4.dp)
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
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                        setTextAppearance(R.style.MainItemTextAppearance)
                                    }
                                    width = maxWidth
                                    id = choice.choice_id.toInt()
                                    text = choice.choice.trim()
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

                                choiceID.text = choice.choice_id
                                choiceName.text = choice.choice.trim()
                                choiceStatus.text = choice.status
                                choiceParent.setOnClickListener {
                                    choiceRadio.isChecked = !choiceRadio.isChecked
                                }

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
            } ?: showEmptyState()
        }
    }

    private fun showEmptyState() {
        binding.apply {
            evaluationLoadingLargeState.isGone = true
            evaluationEmptyState.isVisible = true
        }
    }

    private fun renderLoader(state: Boolean = true) {
        binding.apply {
            evaluationEmptyState.isGone = state
            evaluationLoadingLargeState.isVisible = state
        }
    }

    private fun renderBannerLoader(state: Boolean = true) {
        binding.apply {
            evaluationsSubmit.isGone = state
            evaluationsSubmitBannerState.isVisible = state
        }
    }

    private fun setOnClickListener() {
        binding.apply {
            evaluationsSubmit.setOnClickListener {
                val answerArray = JSONArray()

                val examAnswers = mutableListOf<ExamAnswers>()

                examQuestions?.let { questionResult ->
                    repeat(questionResult.exam_questions.size, action = { questionPosition ->
                        val examQuestion = questionResult.exam_questions[questionPosition]
                        val currentQuestion = evaluationQuestions.getChildAt(questionPosition)
                        try {
                            val answerObject = JSONObject()

                            when {
                                examQuestion.type.equals("1", ignoreCase = true) -> {
                                    val radioBoolean = currentQuestion.findViewById<RadioGroup>(R.id.radioBoolean)
                                    val selectedId = radioBoolean.checkedRadioButtonId

                                    examAnswers.add(
                                        ExamAnswers(
                                            question_id = examQuestion.question_id,
                                            answer = selectedId.toString()
                                        )
                                    )

                                    answerObject.put("question_id", examQuestion.question_id)
                                    answerObject.put("answer", selectedId.toString())
                                    answerArray.put(answerObject)
                                }
                                examQuestion.type.equals("2", ignoreCase = true) -> {
                                    val radioMultiple = currentQuestion.findViewById<RadioGroup>(R.id.radioMultiple)
                                    val selectedId = radioMultiple.checkedRadioButtonId

                                    examAnswers.add(
                                        ExamAnswers(
                                            question_id = examQuestion.question_id,
                                            answer = selectedId.toString()
                                        )
                                    )

                                    answerObject.put("question_id", examQuestion.question_id)
                                    answerObject.put("answer", selectedId.toString())
                                    answerArray.put(answerObject)
                                }
                                examQuestion.type.equals("3", ignoreCase = true) -> {
                                    val multiChoiceList = currentQuestion.findViewById<LinearLayout>(R.id.multiChoiceList)
                                    val choiceArray = mutableListOf<String>()
                                    val count = multiChoiceList.childCount
                                    for (i in 0 until count) {
                                        val row = multiChoiceList.getChildAt(i)
                                        val choiceID = row.findViewById<TextView>(R.id.choiceID)
                                        val choiceName = row.findViewById<TextView>(R.id.choiceName)
                                        val choiceRadio = row.findViewById<CheckBox>(R.id.choiceRadio)

                                        if (choiceRadio.isChecked) {
                                            if (!choiceArray.toString()
                                                .contains(choiceName.text.toString().trim { it <= ' ' })
                                            ) {
                                                choiceArray.add(choiceID.text.toString().trim { it <= ' ' })
                                            }
                                        }
                                    }

                                    examAnswers.add(
                                        ExamAnswers(
                                            question_id = examQuestion.question_id,
                                            answer = choiceArray.toString()
                                        )
                                    )
                                    answerObject.put("question_id", examQuestion.question_id)
                                    answerObject.put("answer", choiceArray.toString())
                                    answerArray.put(answerObject)
                                }
                                else -> {
                                    val answerInput = currentQuestion.findViewById<EditText>(R.id.answerInput)

                                    examAnswers.add(
                                        ExamAnswers(
                                            question_id = examQuestion.question_id,
                                            answer = answerInput.text.toString().trim { it <= ' ' }
                                        )
                                    )
                                    answerObject.put("question_id", examQuestion.question_id)
                                    answerObject.put("answer", answerInput.text.toString().trim { it <= ' ' })
                                    answerArray.put(answerObject)
                                }
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    })
                }

                println("Answers $answerArray")

                viewModel.submitExamAnswers(
                    answers = answerArray
                )
            }
        }
    }
}
