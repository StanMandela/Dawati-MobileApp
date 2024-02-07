package com.ke.dawaati.uiux.validated.content.evaluations.reports

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavDirections
import com.ke.dawaati.api.DawatiAPI
import com.ke.dawaati.api.response.AttemptDetailsResult
import com.ke.dawaati.db.model.EvaluationsAttemptsEntity
import com.ke.dawaati.db.repo.EvaluationsRepository
import com.ke.dawaati.db.repo.UserDetailsRepository
import com.ke.dawaati.preference.ConfigurationPrefs
import com.ke.dawaati.util.Event
import com.ke.dawaati.util.asEvent
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

class EvaluationsReportViewModel(
    private val dawatiAPI: DawatiAPI,
    private val configurationPrefs: ConfigurationPrefs,
    private val userDetailsRepository: UserDetailsRepository,
    private val evaluationsRepository: EvaluationsRepository
) : ViewModel() {

    private val _uiState = MutableLiveData<EvaluationsReportUIState>()
    val uiState: LiveData<EvaluationsReportUIState> = _uiState

    private val _action = MutableLiveData<Event<EvaluationsReportActions>>()
    val action: LiveData<Event<EvaluationsReportActions>> = _action

    private val exceptionHandler = CoroutineExceptionHandler { _, exception ->
        Timber.e(exception)
        _uiState.postValue(EvaluationsReportUIState.Error(statusCode = false, message = exception.message!!))
    }

    private var evaluationAttemptsRequested = false

    fun loadExamAttempts() {
        val allAttempts = evaluationsRepository.loadEvaluationAttempts()

        if (allAttempts.isEmpty() && !evaluationAttemptsRequested) {
            _uiState.postValue(EvaluationsReportUIState.Loading)
            fetchAllEvaluationAttempts()
        } else {
            _uiState.postValue(EvaluationsReportUIState.AllAttempts(examAttempts = allAttempts))
            if (!evaluationAttemptsRequested) {
                fetchAllEvaluationAttempts()
            }
        }
    }

    private fun fetchAllEvaluationAttempts() {
        evaluationAttemptsRequested = true
        val user = userDetailsRepository.loadUserDetails()
        _uiState.postValue(EvaluationsReportUIState.Loading)
        viewModelScope.launch(exceptionHandler) {
            val responseAllEvaluationAttempts = withContext(Dispatchers.IO) {
                dawatiAPI.fetchEvaluationAttempts(user_id = user?.user_id ?: "")
            }

            if (responseAllEvaluationAttempts.status) {
                evaluationsRepository.deleteEvaluationAttempts()
                val allAttempts = responseAllEvaluationAttempts.result!!.all_attempts.distinct()
                allAttempts.forEach {
                    evaluationsRepository.insertEvaluationAttempts(
                        EvaluationsAttemptsEntity(
                            exam_id = it.exam_id,
                            exam_name = it.exam_name,
                            subject = it.subject,
                            user_score = it.user_score,
                            response_id = it.response_id
                        )
                    )
                }
            }

            loadExamAttempts()
        }
    }

    fun viewPreviousAttemptDetails(evaluationName: String, exam_id: String, response_id: String) {
        _action.postValue(
            EvaluationsReportActions
                .Navigate(
                    EvaluationsReportListFragmentDirections.toEvaluationsReportDetailsFragment(
                        evaluationName = evaluationName,
                        examID = exam_id,
                        responseID = response_id
                    )
                ).asEvent()
        )
    }

    fun loadPreviousAttemptDetails(exam_id: String, response_id: String) {
        val user = userDetailsRepository.loadUserDetails()
        viewModelScope.launch(exceptionHandler) {
            val responseSubjectExams = withContext(Dispatchers.IO) {
                dawatiAPI.fetchEvaluationReport(
                    user_id = user?.user_id ?: "",
                    exam_id = exam_id,
                    attempt_id = response_id
                )
            }

            _uiState.postValue(
                EvaluationsReportUIState.EvaluationReport(
                    examAttempts = responseSubjectExams.result
                )
            )
        }
    }
}

sealed class EvaluationsReportActions {
    data class Navigate(val destination: NavDirections) : EvaluationsReportActions()
}

sealed class EvaluationsReportUIState {
    object Loading : EvaluationsReportUIState()

    data class AllAttempts(val examAttempts: List<EvaluationsAttemptsEntity>) : EvaluationsReportUIState()

    data class EvaluationReport(val examAttempts: AttemptDetailsResult?) : EvaluationsReportUIState()

    data class Error(val statusCode: Boolean, val message: String) : EvaluationsReportUIState()
}
