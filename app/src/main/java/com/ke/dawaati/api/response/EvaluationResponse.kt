package com.ke.dawaati.api.response

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

@JsonClass(generateAdapter = true)
data class EvaluationResponse(
    @Json(name = "status")
    val status: Boolean,
    @Json(name = "result")
    val result: EvaluationResult? = null
)

@JsonClass(generateAdapter = true)
data class EvaluationResult(
    @Json(name = "subject")
    val subject: String = "",
    @Json(name = "exams")
    val exams: List<Exams> = emptyList()
)

@JsonClass(generateAdapter = true)
data class Exams(
    @Json(name = "exam_id")
    val exam_id: String = "",
    @Json(name = "exam_name")
    val exam_name: String = "",
    @Json(name = "hours")
    val hours: String = "",
    @Json(name = "minutes")
    val minutes: String = ""
)

/**
 * Evaluation questions
 */
@JsonClass(generateAdapter = true)
data class EvaluationQuestionsResponse(
    @Json(name = "status")
    val status: Boolean,
    @Json(name = "result")
    val result: EvaluationQuestionsResult? = null
)

@JsonClass(generateAdapter = true)
data class EvaluationQuestionsResult(
    @Json(name = "exam_details")
    val exam_details: ExamDetails? = null,
    @Json(name = "exam_questions")
    val exam_questions: List<ExamQuestions> = emptyList(),
    @Json(name = "exam_choices")
    val exam_choices: List<ExamChoices> = emptyList()
)

@JsonClass(generateAdapter = true)
data class ExamDetails(
    @Json(name = "exam_id")
    val exam_id: String = "",
    @Json(name = "exam_name")
    val exam_name: String = "",
    @Json(name = "description")
    val description: String = "",
    @Json(name = "hours")
    val hours: String = "",
    @Json(name = "minutes")
    val minutes: String = "",
    @Json(name = "num_of_questions")
    val num_of_questions: String = "",
    @Json(name = "file_url")
    val file_url: String = ""
)

@JsonClass(generateAdapter = true)
data class ExamQuestions(
    @Json(name = "question_id")
    val question_id: String = "",
    @Json(name = "question")
    val question: String = "",
    @Json(name = "type")
    val type: String = "",
    @Json(name = "score")
    val score: String = "",
    @Json(name = "exam_id")
    val exam_id: String = "",
    @Json(name = "attachment")
    val attachment: String = "",
    @Json(name = "num_answers")
    val num_answers: String = "",
    @Json(name = "subtopicID")
    val subtopicID: String = "",
    @Json(name = "name")
    val name: String = ""
)

@JsonClass(generateAdapter = true)
data class ExamChoices(
    @Json(name = "choice_id")
    val choice_id: String = "",
    @Json(name = "choice")
    val choice: String = "",
    @Json(name = "status")
    val status: String = "",
    @Json(name = "question_id")
    val question_id: String = ""
)

/**
 * Evaluation all attempts
 */
@JsonClass(generateAdapter = true)
data class EvaluationAllAttemptsResponse(
    @Json(name = "status")
    val status: Boolean,
    @Json(name = "result")
    val result: EvaluationAllAttemptsResult? = null
)

@JsonClass(generateAdapter = true)
data class EvaluationAllAttemptsResult(
    @Json(name = "all_attempts")
    val all_attempts: List<EvaluationAllAttempts> = emptyList()
)

@JsonClass(generateAdapter = true)
data class EvaluationAllAttempts(
    @Json(name = "exam_id")
    val exam_id: String = "",
    @Json(name = "exam_name")
    val exam_name: String = "",
    @Json(name = "subject")
    val subject: String = "",
    @Json(name = "user_score")
    val user_score: String = "",
    @Json(name = "response_id")
    val response_id: String = ""
)

/**
 * Evaluation attempt details
 */
@JsonClass(generateAdapter = true)
data class EvaluationAttemptDetailsResponse(
    @Json(name = "status")
    val status: Boolean,
    @Json(name = "result")
    val result: AttemptDetailsResult? = null
)

@JsonClass(generateAdapter = true)
data class AttemptDetailsResult(
    @Json(name = "questions")
    val questions: List<AttemptExamQuestions> = emptyList(),
    @Json(name = "questions_attachment")
    val questions_attachment: List<String> = emptyList(),
    @Json(name = "answers")
    val answers: List<ExamChoices> = emptyList(),
    @Json(name = "student_answers")
    val student_answers: List<StudentAnswers> = emptyList(),
    @Json(name = "correct_answer")
    val correct_answer: List<String> = emptyList(),
    @Json(name = "questions_scores")
    val questions_scores: List<String> = emptyList(),
    @Json(name = "total_score")
    val total_score: Int = 0,
    @Json(name = "remarks")
    val remarks: String = ""
)

// @Json(name = "correct_answer")
// val correct_answer: List<CorrectAnswers> = emptyList(),

@JsonClass(generateAdapter = true)
data class AttemptExamQuestions(
    @Json(name = "exam_id")
    val exam_id: String = "",
    @Json(name = "topicID")
    val topicID: String = "",
    @Json(name = "topic_name")
    val topic_name: String = "",
    @Json(name = "study_level")
    val study_level: String = "",
    @Json(name = "subtopicID")
    val subtopicID: String = "",
    @Json(name = "questionID")
    val question_id: String = "",
    @Json(name = "question")
    val question: String = "",
    @Json(name = "type")
    val type: String = "",
    @Json(name = "score")
    val score: String = "",
    @Json(name = "num_answers")
    val num_answers: String = ""
)

@JsonClass(generateAdapter = true)
data class EvaluationAttemptQuestionAttachment(
    @Json(name = "status")
    val status: Boolean,
    @Json(name = "result")
    val result: AttemptDetailsResult? = null
)

@JsonClass(generateAdapter = true)
data class StudentAnswers(
    @Json(name = "attempt_id")
    val attempt_id: String = "",
    @Json(name = "question_id")
    val question_id: String = "",
    @Json(name = "choice_id")
    val choice_id: String = "",
    @Json(name = "test_respondent")
    val test_respondent: String = "",
    @Json(name = "status")
    val status: String = "",
    @Json(name = "score")
    val score: String = "",
    @Json(name = "comment")
    val comment: String = ""
)

@JsonClass(generateAdapter = true)
data class CorrectAnswers(
    @Json(name = "choice_id")
    val choice_id: String = "",
    @Json(name = "question_id")
    val question_id: String = "",
    @Json(name = "answer")
    val answer: String = ""
)

@JsonClass(generateAdapter = true)
data class QuestionScores(
    @Json(name = "score")
    val score: String = "",
    @Json(name = "question_id")
    val question_id: String = ""
)

/**
 * Submit evaluations response
 */
@JsonClass(generateAdapter = true)
data class SubmitEvaluationResponse(
    @Json(name = "status")
    val status: Boolean = false,
    @Json(name = "exam_results")
    val examResults: List<SubmitEvaluationExamResults> = emptyList()
)

@Parcelize
@JsonClass(generateAdapter = true)
data class SubmitEvaluationExamResults(
    @Json(name = "question_id")
    val question_id: String = "",
    @Json(name = "question")
    val question: String = "",
    @Json(name = "type")
    val type: String = "",
    @Json(name = "attachment")
    val attachment: String = "",
    @Json(name = "full_mark")
    val full_mark: String = "",
    @Json(name = "options")
    val options: String = "",
    @Json(name = "your_answer")
    val your_answer: String = "",
    @Json(name = "your_mark")
    val your_mark: String = "",
    @Json(name = "comment")
    val comment: String = "",
    @Json(name = "file_url")
    val file_url: String = ""
) : Parcelable
