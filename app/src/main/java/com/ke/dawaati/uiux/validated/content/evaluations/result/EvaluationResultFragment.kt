package com.ke.dawaati.uiux.validated.content.evaluations.result

import android.content.Context
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.google.android.material.textfield.TextInputLayout
import com.ke.dawaati.R
import com.ke.dawaati.api.response.ExamChoices
import com.ke.dawaati.api.response.SubmitEvaluationExamResults
import com.ke.dawaati.databinding.EvaluationsResultFragmentBinding
import com.ke.dawaati.util.dp
import com.ke.dawaati.util.viewBinding

class EvaluationResultFragment : Fragment(R.layout.evaluations_result_fragment) {

    private val binding by viewBinding(EvaluationsResultFragmentBinding::bind)
    private val args: EvaluationResultFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        setOnClickListener()

        renderResult(examResults = args.submitEvaluationExamResults.toList())
    }

    private fun setupToolbar() {
        binding.apply {
            toolbar.setNavigationOnClickListener { findNavController().navigateUp() }
            toolbarTitle.text = args.evaluationName
        }
    }

    private fun renderResult(examResults: List<SubmitEvaluationExamResults>) {
        var questionNumber = 0
        binding.apply {
            examResults.forEach { singleQuestion ->
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
                        .load(singleQuestion.file_url + singleQuestion.attachment)
                        .into(questionImage)
                    questionImage.isVisible = true
                }

                choicesTitle.isVisible =
                    singleQuestion.type.equals("2", ignoreCase = true) || singleQuestion.type.equals("1", ignoreCase = true)

                val correctStateList = ColorStateList(
                    arrayOf(
                        intArrayOf(-android.R.attr.state_checked),
                        intArrayOf(android.R.attr.state_checked)
                    ),
                    intArrayOf(
                        ContextCompat.getColor(requireContext(), R.color.passengerOne),
                        ContextCompat.getColor(requireContext(), R.color.passengerOne)
                    )
                )

                val incorrectStateList = ColorStateList(
                    arrayOf(
                        intArrayOf(-android.R.attr.state_checked),
                        intArrayOf(android.R.attr.state_checked)
                    ),
                    intArrayOf(
                        ContextCompat.getColor(requireContext(), R.color.passengerFive),
                        ContextCompat.getColor(requireContext(), R.color.passengerFive)
                    )
                )

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

                        /*multiChoices.forEach { choice ->
                            val choiceButton = RadioButton(requireContext())
                            choiceButton.apply {
                                setTextAppearance(R.style.MainItemTextAppearance)
                                textSize = 16.toFloat()
                                width = maxWidth
                                id = choice.choice_id.toInt()
                                text = choice.choice.trim()
                                setPadding(0.dp, 4.dp, 0.dp, 4.dp)
                            }
                            radioBoolean.addView(choiceButton)
                        }*/
                    }
                    singleQuestion.type.equals("2", ignoreCase = true) -> {
                        radioMultiple.apply {
                            isVisible = true
                            orientation = LinearLayout.VERTICAL
                        }
                        instructions.text = "* Choose one answer *"

                        val options = singleQuestion.options.split(",")
                        val examChoices = mutableListOf<ExamChoices>()
                        options.forEach { option ->
                            val splitOption = option.split("|")
                            examChoices.add(
                                ExamChoices(
                                    choice_id = splitOption[0],
                                    choice = splitOption[2],
                                    status = splitOption[1],
                                    question_id = singleQuestion.question_id
                                )
                            )
                        }

                        examChoices.forEach { choice ->
                            val choiceButton = RadioButton(requireContext())
                            choiceButton.apply {
                                setTextAppearance(R.style.MainItemTextAppearance)
                                width = maxWidth
                                id = choice.choice_id.toInt()
                                text = choice.choice.trim()
                                isChecked = singleQuestion.your_answer.equals(choice.choice_id, ignoreCase = true)

                                if (choice.status.equals("Correct", ignoreCase = true)) {
                                    setTextColor(
                                        ContextCompat.getColor(
                                            requireContext(), R.color.passengerOne
                                        )
                                    )
                                    buttonTintList = correctStateList
                                }

                                if (singleQuestion.your_answer.equals(choice.choice_id, ignoreCase = true) && choice.status.equals("Correct", ignoreCase = true)) {
                                    setTextColor(
                                        ContextCompat.getColor(
                                            requireContext(),
                                            R.color.passengerOne
                                        )
                                    )
                                    buttonTintList = correctStateList
                                } else if (singleQuestion.your_answer.equals(choice.choice_id, ignoreCase = true) && choice.status.equals("Incorrect", ignoreCase = true)) {
                                    setTextColor(
                                        ContextCompat.getColor(
                                            requireContext(),
                                            R.color.passengerFive
                                        )
                                    )
                                    buttonTintList = incorrectStateList
                                }
                            }
                            radioMultiple.addView(choiceButton)
                        }
                    }
                    singleQuestion.type.equals("3", ignoreCase = true) -> {
                        multiChoiceList.isVisible = true
                        multiChoiceList.removeAllViews()
                        instructions.text = "* Choose one or more choices *"

//                        multiChoices.forEach { choice ->
//                            val choiceInflater =
//                                requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
//                            val multiView =
//                                choiceInflater.inflate(R.layout.item_answer_picker, null)
//                            val choiceID = multiView.findViewById<TextView>(R.id.choiceID)
//                            val choiceName = multiView.findViewById<TextView>(R.id.choiceName)
//                            val choiceStatus = multiView.findViewById<TextView>(R.id.choiceStatus)
//                            val choiceRadio = multiView.findViewById<MaterialCheckBox>(R.id.choiceRadio)
//                            val choiceParent = multiView.findViewById<ConstraintLayout>(R.id.choiceParent)
//
//                            choiceID.text = choice.choice_id
//                            choiceName.text = choice.choice.trim()
//                            choiceStatus.text = choice.status
//                            choiceParent.setOnClickListener {
//                                choiceRadio.isChecked = !choiceRadio.isChecked
//                            }
//
//                            multiView.isClickable = true
//
//                            multiChoiceList.addView(multiView, multiChoiceList.childCount)
//                        }
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

                evaluationResult.addView(rowView, evaluationResult.childCount)
            }
        }
    }

    private fun setOnClickListener() {
        binding.apply {
            evaluationsSubmit.setOnClickListener {
                findNavController().navigateUp()
            }
        }
    }
}
