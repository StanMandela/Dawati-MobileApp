package com.ke.dawaati.uiux.validated.content.evaluations

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavDirections
import com.ke.dawaati.api.DawatiAPI
import com.ke.dawaati.db.model.EvaluationsEntity
import com.ke.dawaati.db.repo.EvaluationsRepository
import com.ke.dawaati.preference.ConfigurationPrefs
import com.ke.dawaati.uiux.validated.content.SubjectContentFragmentDirections
import com.ke.dawaati.util.Event
import com.ke.dawaati.util.asEvent
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

class ContentEvaluationViewModel(
    private val dawatiAPI: DawatiAPI,
    private val evaluationsRepository: EvaluationsRepository,
    private val configurationPrefs: ConfigurationPrefs
) : ViewModel() {

    private val _uiState = MutableLiveData<ContentEvaluationUIState>()
    val uiState: LiveData<ContentEvaluationUIState> = _uiState

    private val _action = MutableLiveData<Event<ContentEvaluationActions>>()
    val action: LiveData<Event<ContentEvaluationActions>> = _action

    private val exceptionHandler = CoroutineExceptionHandler { _, exception ->
        Timber.e(exception)
        _uiState.postValue(ContentEvaluationUIState.Error(statusCode = false, message = exception.message!!))
    }

    private var examsRequested = false

    fun loadAllExam() {
        val evaluations = evaluationsRepository
            .loadEvaluations(subject_id = configurationPrefs.getSubject().first!!)

        if (evaluations.isEmpty() && !examsRequested) {
            fetchAllExams()
        } else {
            val displayEvaluations = mutableListOf<DisplayEvaluations>()
            evaluations.forEach { evaluation ->
                displayEvaluations.add(evaluation.toEvaluationsDisplay())
            }
            _uiState.postValue(ContentEvaluationUIState.Evaluations(evaluations = displayEvaluations))
        }
    }

    private fun fetchAllExams() {
        _uiState.postValue(ContentEvaluationUIState.Loading)
        viewModelScope.launch(exceptionHandler) {
            val responseSubjectExams = withContext(Dispatchers.IO) {
                dawatiAPI.fetchAllSubjectExams(subject = configurationPrefs.getSubject().first!!)
            }

            if (responseSubjectExams.status) {
                val evaluations = responseSubjectExams.result!!.exams
                val subjectID = responseSubjectExams.result.subject
                if (evaluations.isNotEmpty()) {
                    evaluationsRepository.deleteEvaluations(
                        subject_id = subjectID
                    )
                    evaluations.forEach { evaluation ->
                        evaluationsRepository.insertEvaluations(
                            evaluationsEntity = EvaluationsEntity(
                                subject_id = configurationPrefs.getSubject().first!!,
                                subject_name = configurationPrefs.getSubject().second!!,
                                exam_id = evaluation.exam_id,
                                exam_name = evaluation.exam_name,
                                hours = evaluation.hours,
                                minutes = evaluation.minutes
                            )
                        )
                    }
                }
            }

            examsRequested = true
            loadAllExam()
        }
    }

    fun viewExam(displayEvaluations: DisplayEvaluations) {
        configurationPrefs.setExam(examID = displayEvaluations.exam_id)
        _action.postValue(
            ContentEvaluationActions
                .Navigate(
                    SubjectContentFragmentDirections.toEvaluationAttemptFragment(
                        evaluationName = displayEvaluations.exam_name
                    )
                )
                .asEvent()
        )
    }

    fun viewPreviousAttempts() {
        _action.postValue(
            ContentEvaluationActions
                .Navigate(
                    SubjectContentFragmentDirections.toEvaluationsReportListFragment()
                )
                .asEvent()
        )
    }
}

sealed class ContentEvaluationActions {
    data class Navigate(val destination: NavDirections) : ContentEvaluationActions()
}

sealed class ContentEvaluationUIState {
    object Loading : ContentEvaluationUIState()

    data class Evaluations(val evaluations: List<DisplayEvaluations>) : ContentEvaluationUIState()

    data class Error(val statusCode: Boolean, val message: String) : ContentEvaluationUIState()
}

data class DisplayEvaluations(
    var exam_id: String = "",
    var exam_name: String = "",
    var exam_duration: String = ""
)

fun EvaluationsEntity.toEvaluationsDisplay() = DisplayEvaluations(
    exam_id = exam_id,
    exam_name = exam_name,
    exam_duration = "Duration: $hours hour $minutes minutes"
)
