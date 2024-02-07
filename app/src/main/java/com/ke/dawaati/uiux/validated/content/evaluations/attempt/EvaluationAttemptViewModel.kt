package com.ke.dawaati.uiux.validated.content.evaluations.attempt

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavDirections
import com.ke.dawaati.api.DawatiAPI
import com.ke.dawaati.api.response.EvaluationQuestionsResult
import com.ke.dawaati.db.repo.UserDetailsRepository
import com.ke.dawaati.preference.ConfigurationPrefs
import com.ke.dawaati.util.Event
import com.ke.dawaati.util.asEvent
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import timber.log.Timber

class EvaluationAttemptViewModel(
    private val dawatiAPI: DawatiAPI,
    private val configurationPrefs: ConfigurationPrefs,
    private val userDetailsRepository: UserDetailsRepository
) : ViewModel() {

    private val _uiState = MutableLiveData<EvaluationAttemptUIState>()
    val uiState: LiveData<EvaluationAttemptUIState> = _uiState

    private val _action = MutableLiveData<Event<EvaluationAttemptActions>>()
    val action: LiveData<Event<EvaluationAttemptActions>> = _action

    private val exceptionHandler = CoroutineExceptionHandler { _, exception ->
        Timber.e(exception)
        _uiState.postValue(
            EvaluationAttemptUIState.Error(
                statusCode = false, message = exception.message ?: ""
            )
        )
    }

    fun fetchAllExams() {
        _uiState.postValue(EvaluationAttemptUIState.Loading)
        viewModelScope.launch(exceptionHandler) {
            val responseSubjectExams = withContext(Dispatchers.IO) {
                dawatiAPI.fetchSingleExam(exam_id = configurationPrefs.getExam()!!)
            }

            if (responseSubjectExams.status) {
                _uiState.postValue(
                    EvaluationAttemptUIState
                        .Questions(examQuestions = responseSubjectExams.result)
                )
            } else {
            }
        }
    }

    // answers: MutableList<ExamAnswers>
    fun submitExamAnswers(answers: JSONArray) {
        _uiState.postValue(EvaluationAttemptUIState.SubmitLoading)

        viewModelScope.launch(exceptionHandler) {
            val responseSubjectExams = withContext(Dispatchers.IO) {
                dawatiAPI.submitEvaluation(
                    user_id = userDetailsRepository.loadUserDetails()?.user_id ?: "",
                    exam_id = configurationPrefs.getExam() ?: "",
                    response = answers.toString()
                )
            }

            if (responseSubjectExams.status) {
                var fullMark = 0
                var yourMark = 0
                val examResults = responseSubjectExams.examResults

                examResults.forEach { result ->
                    fullMark += result.full_mark.toInt()
                    yourMark += result.your_mark.toInt()
                }

                _uiState.postValue(
                    EvaluationAttemptUIState.EvaluationResponse(
                        fullMark = fullMark.toString(),
                        yourMark = yourMark.toString()
                    )
                )
                _action.postValue(
                    EvaluationAttemptActions.Navigate(
                        destination = EvaluationAttemptFragmentDirections.toEvaluationResultFragment(
                            evaluationName = "",
                            submitEvaluationExamResults = examResults.toTypedArray()
                        )
                    ).asEvent()
                )
            } else {
                _uiState.postValue(
                    EvaluationAttemptUIState.Error(
                        statusCode = false, message = ""
                    )
                )
            }
        }
    }
}

sealed class EvaluationAttemptActions {
    data class Navigate(val destination: NavDirections) : EvaluationAttemptActions()
}

sealed class EvaluationAttemptUIState {
    object Loading : EvaluationAttemptUIState()

    object SubmitLoading : EvaluationAttemptUIState()

    data class Questions(val examQuestions: EvaluationQuestionsResult?) : EvaluationAttemptUIState()

    data class EvaluationResponse(
        val fullMark: String,
        val yourMark: String
    ) : EvaluationAttemptUIState()

    data class Error(val statusCode: Boolean, val message: String) : EvaluationAttemptUIState()
}
