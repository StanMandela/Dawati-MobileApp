package com.ke.dawaati.uiux.validated.content.videos

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavDirections
import com.ke.dawaati.api.DawatiAPI
import com.ke.dawaati.db.model.PracticalsEntity
import com.ke.dawaati.db.model.SubTopicsEntity
import com.ke.dawaati.db.model.TopicsEntity
import com.ke.dawaati.db.repo.ContentRepository
import com.ke.dawaati.db.repo.UserDetailsRepository
import com.ke.dawaati.preference.ConfigurationPrefs
import com.ke.dawaati.uiux.validated.content.SubjectContentFragmentDirections
import com.ke.dawaati.util.Event
import com.ke.dawaati.util.StringConstants.SECTIONED
import com.ke.dawaati.util.asEvent
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

class ContentVideosViewModel(
    private val dawatiAPI: DawatiAPI,
    private val contentRepository: ContentRepository,
    private val configurationPrefs: ConfigurationPrefs,
    userDetailsRepository: UserDetailsRepository
) : ViewModel() {

    private val _uiState = MutableLiveData<ContentVideosUIState>()
    val uiState: LiveData<ContentVideosUIState> = _uiState

    private val _action = MutableLiveData<Event<ContentVideosActions>>()
    val action: LiveData<Event<ContentVideosActions>> = _action

    private val exceptionHandler = CoroutineExceptionHandler { _, exception ->
        Timber.e(exception)
        _uiState.postValue(ContentVideosUIState.Error(statusCode = false, message = exception.message!!))
    }

    private var classVideosRequested = false
    private var labVideosRequested = false
    private var examsVideosRequested = false
    private var subscriptionStatus = (userDetailsRepository.loadUserDetails()?.subscription_status ?: "").equals("active", ignoreCase = true)

    fun getSubject() = configurationPrefs.getSubject().second!!
    fun getLevel() = configurationPrefs.getLevel().second!!

    fun viewVideo(file_id: String, direction: String = SECTIONED) {
        configurationPrefs.setVideos(videoID = file_id)
        configurationPrefs.setVideoDirection(direction = direction)
        _action.postValue(
            ContentVideosActions.Navigate(
                SubjectContentFragmentDirections.toContentVideoDetailsFragment()
            ).asEvent()
        )
    }

    fun loadVideos() {
        val videoTopics = contentRepository
            .loadTopics(
                topicCategory = "Class",
                subject_id = configurationPrefs.getSubject().first!!,
                level_id = configurationPrefs.getLevel().first!!
            )
        val videoSubTopics = contentRepository
            .loadSubTopics(
                topicCategory = "Class",
                subject_id = configurationPrefs.getSubject().first!!,
                level_id = configurationPrefs.getLevel().first!!
            )

        if (videoTopics.isEmpty() && !classVideosRequested) {
            _uiState.postValue(ContentVideosUIState.Loading)
            fetchClassVideos()
        } else {
            val displayVideos = mutableListOf<DisplayVideoTopics>()

            videoTopics.forEach { topicsEntity ->
                val subTopics = videoSubTopics.filter {
                    it.topicID.equals(
                        topicsEntity.topicID,
                        ignoreCase = true
                    )
                }
                val displayVideoSubTopics = mutableListOf<DisplayVideoSubTopics>()
                subTopics.forEach {
                    displayVideoSubTopics.add(
                        DisplayVideoSubTopics(
                            id = it.id,
                            topicID = it.topicID,
                            indexID = it.indexID,
                            subTopicID = it.subTopicID,
                            name = it.name,
                            availability = it.availability,
                            file_name = it.file_name,
                            views = it.views,
                            file_id = it.file_id,
                            subject = it.subject,
                            file_url = it.file_url,
                            subscriptionStatus = subscriptionStatus
                        )
                    )
                }
                displayVideos.add(
                    DisplayVideoTopics(
                        id = topicsEntity.id,
                        name = topicsEntity.name,
                        topicID = topicsEntity.topicID,
                        subTopicVideos = displayVideoSubTopics
                    )
                )
            }
            _uiState.postValue(
                ContentVideosUIState
                    .VideosContent(
                        displayVideos = displayVideos,
                        updateRequest = classVideosRequested,
                        subject = configurationPrefs.getSubject().second!!,
                        level = configurationPrefs.getLevel().second!!
                    )
            )
            if (!classVideosRequested) {
                fetchClassVideos()
            }
        }
    }

    private fun fetchClassVideos() {
        classVideosRequested = true
        viewModelScope.launch(exceptionHandler) {
            val responseClassVideos = withContext(Dispatchers.IO) {
                dawatiAPI.fetchClassVideos(
                    level = configurationPrefs.getLevel().first!!,
                    subject = configurationPrefs.getSubject().second!!
                )
            }

            if (responseClassVideos.result!!.status.equals("true", ignoreCase = true)) {
                val topics = responseClassVideos.result.topics
                if (topics.isNotEmpty()) {
                    topics.forEach { videoTopics ->
                        contentRepository.deleteTopics(
                            topicID = videoTopics.topicID
                        )
                        contentRepository.insertTopics(
                            topicsEntity = TopicsEntity(
                                name = videoTopics.name,
                                topicID = videoTopics.topicID,
                                topicCategory = "Class",
                                level_id = configurationPrefs.getLevel().first!!,
                                level_name = configurationPrefs.getLevel().second!!,
                                subject_id = configurationPrefs.getSubject().first!!,
                                subject_name = configurationPrefs.getSubject().second!!
                            )
                        )

                        val subTopics = videoTopics.subtopics
                        subTopics.forEach { videoSubTopics ->
                            contentRepository.deleteSubTopics(
                                topicID = videoTopics.topicID,
                                file_id = videoSubTopics.file_id
                            )
                            contentRepository.insertSubTopics(
                                subTopicsEntity = SubTopicsEntity(
                                    topicCategory = "Class",
                                    topicID = videoTopics.topicID,
                                    indexID = videoSubTopics.indexID,
                                    subTopicID = videoSubTopics.subtopicID,
                                    name = videoSubTopics.name,
                                    availability = videoSubTopics.availability,
                                    file_name = videoSubTopics.file_name,
                                    views = videoSubTopics.views,
                                    file_id = videoSubTopics.file_id,
                                    subject = videoSubTopics.subject,
                                    file_url = videoSubTopics.file_url,
                                    level_id = configurationPrefs.getLevel().first!!,
                                    level_name = configurationPrefs.getLevel().second!!,
                                    subject_id = configurationPrefs.getSubject().first!!,
                                    subject_name = configurationPrefs.getSubject().second!!
                                )
                            )
                        }
                    }
                }
            } else {}

            loadVideos()
        }
    }

    fun loadLabPracticalVideos() {
        val videoTopics = contentRepository.loadPracticals(
            subject_id = configurationPrefs.getSubject().first!!,
            level_id = configurationPrefs.getLevel().first!!
        )

        if (videoTopics.isEmpty()) {
            _uiState.postValue(ContentVideosUIState.Loading)
            fetchLabPracticalVideos()
        } else {
            _uiState.postValue(
                ContentVideosUIState.PracticalContent(
                    displayVideos = videoTopics,
                    updateRequest = labVideosRequested,
                    subject = configurationPrefs.getSubject().second ?: "",
                    level = configurationPrefs.getLevel().second ?: "",
                    subscriptionStatus = subscriptionStatus
                )
            )
            if (!labVideosRequested) {
                fetchLabPracticalVideos()
            }
        }
    }

    private fun fetchLabPracticalVideos() {
        labVideosRequested = true
        _uiState.postValue(ContentVideosUIState.Loading)
        viewModelScope.launch(exceptionHandler) {
            val responseClassVideos = withContext(Dispatchers.IO) {
                dawatiAPI.fetchLabPracticalVideos(
                    level = configurationPrefs.getLevel().first ?: "",
                    subject = configurationPrefs.getSubject().second ?: ""
                )
            }

            responseClassVideos.result?.let { practicalsResult ->
                if (practicalsResult.status.equals("true", ignoreCase = true)) {
                    val videos = practicalsResult.practicals

                    videos.forEach {
                        contentRepository.deletePracticals(
                            file_id = it.file_id
                        )
                        contentRepository.insertPracticals(
                            practicalsEntity = PracticalsEntity(
                                topicID = it.topicID,
                                file_type = it.file_type,
                                file_name = it.file_name,
                                file_id = it.file_id,
                                subtopicID = it.subtopicID,
                                subject = it.subject,
                                availability = it.availability,
                                file_url = it.file_url,
                                subject_id = configurationPrefs.getSubject().first ?: "",
                                subject_name = configurationPrefs.getSubject().second ?: "",
                                level_id = configurationPrefs.getLevel().first ?: "",
                                level_name = configurationPrefs.getLevel().second ?: ""
                            )
                        )
                    }
                }
            }

            loadLabPracticalVideos()
        }
    }

    fun loadExamVideos() {
        val videoTopics = contentRepository
            .loadTopics(
                topicCategory = "Exam review",
                subject_id = configurationPrefs.getSubject().first!!,
                level_id = configurationPrefs.getLevel().first!!
            )
        val videoSubTopics = contentRepository
            .loadSubTopics(
                topicCategory = "Exam review",
                subject_id = configurationPrefs.getSubject().first!!,
                level_id = configurationPrefs.getLevel().first!!
            )

        if (videoTopics.isEmpty() && !examsVideosRequested) {
            _uiState.postValue(ContentVideosUIState.Loading)
            fetchExamVideos()
        } else {
            val displayVideos = mutableListOf<DisplayVideoTopics>()

            videoTopics.forEach { topicsEntity ->
                val subTopics = videoSubTopics.filter {
                    it.topicID.equals(
                        topicsEntity.topicID,
                        ignoreCase = true
                    )
                }
                val displayVideoSubTopics = mutableListOf<DisplayVideoSubTopics>()
                subTopics.forEach {
                    displayVideoSubTopics.add(
                        DisplayVideoSubTopics(
                            id = it.id,
                            topicID = it.topicID,
                            indexID = it.indexID,
                            subTopicID = it.subTopicID,
                            name = it.name,
                            availability = it.availability,
                            file_name = it.file_name,
                            views = it.views,
                            file_id = it.file_id,
                            subject = it.subject,
                            file_url = it.file_url
                        )
                    )
                }
                displayVideos.add(
                    DisplayVideoTopics(
                        id = topicsEntity.id,
                        name = topicsEntity.name,
                        topicID = topicsEntity.topicID,
                        subTopicVideos = displayVideoSubTopics
                    )
                )
            }
            _uiState.postValue(
                ContentVideosUIState
                    .VideosContent(
                        displayVideos = displayVideos,
                        updateRequest = examsVideosRequested,
                        subject = configurationPrefs.getSubject().second!!,
                        level = configurationPrefs.getLevel().second!!
                    )
            )
            if (!examsVideosRequested) {
                fetchExamVideos()
            }
        }
    }

    private fun fetchExamVideos() {
        examsVideosRequested = true
        viewModelScope.launch(exceptionHandler) {
            val responseClassVideos = withContext(Dispatchers.IO) {
                dawatiAPI.fetchExamVideos(
                    level = configurationPrefs.getLevel().first!!,
                    subject = configurationPrefs.getSubject().second!!
                )
            }

            if (responseClassVideos.result!!.status.equals("true", ignoreCase = true)) {
                val topics = responseClassVideos.result.topics
                if (topics.isNotEmpty()) {
                    topics.forEach { videoTopics ->
                        contentRepository.deleteTopics(
                            topicID = videoTopics.topicID
                        )
                        contentRepository.insertTopics(
                            topicsEntity = TopicsEntity(
                                name = videoTopics.name,
                                topicID = videoTopics.topicID,
                                topicCategory = "Exam review",
                                level_id = configurationPrefs.getLevel().first!!,
                                level_name = configurationPrefs.getLevel().second!!,
                                subject_id = configurationPrefs.getSubject().first!!,
                                subject_name = configurationPrefs.getSubject().second!!
                            )
                        )

                        val subTopics = videoTopics.subtopics
                        subTopics.forEach { videoSubTopics ->
                            contentRepository.deleteSubTopics(
                                topicID = videoTopics.topicID,
                                file_id = videoSubTopics.file_id
                            )
                            contentRepository.insertSubTopics(
                                subTopicsEntity = SubTopicsEntity(
                                    topicCategory = "Exam review",
                                    topicID = videoTopics.topicID,
                                    indexID = videoSubTopics.indexID,
                                    subTopicID = videoSubTopics.subtopicID,
                                    name = videoSubTopics.name,
                                    availability = videoSubTopics.availability,
                                    file_name = videoSubTopics.file_name,
                                    views = videoSubTopics.views,
                                    file_id = videoSubTopics.file_id,
                                    subject = videoSubTopics.subject,
                                    file_url = videoSubTopics.file_url,
                                    level_id = configurationPrefs.getLevel().first!!,
                                    level_name = configurationPrefs.getLevel().second!!,
                                    subject_id = configurationPrefs.getSubject().first!!,
                                    subject_name = configurationPrefs.getSubject().second!!
                                )
                            )
                        }
                    }
                }
            } else { }

            loadExamVideos()
        }
    }
}

sealed class ContentVideosActions {
    data class Navigate(val destination: NavDirections) : ContentVideosActions()
}

sealed class ContentVideosUIState {
    object Loading : ContentVideosUIState()

    data class PracticalContent(
        val displayVideos: List<PracticalsEntity>,
        val updateRequest: Boolean,
        val subject: String,
        val level: String,
        val subscriptionStatus: Boolean
    ) : ContentVideosUIState()

    data class VideosContent(
        val displayVideos: List<DisplayVideoTopics>,
        val updateRequest: Boolean,
        val subject: String,
        val level: String
    ) : ContentVideosUIState()

    data class Error(val statusCode: Boolean, val message: String) : ContentVideosUIState()
}

data class DisplayVideoTopics(
    var id: Int = 0,
    var name: String = "",
    var topicID: String = "",
    var subTopicVideos: List<DisplayVideoSubTopics> = emptyList()
)

data class DisplayVideoSubTopics(
    var id: Int = 0,
    var topicID: String = "",
    var indexID: String = "",
    var subTopicID: String = "",
    var name: String = "",
    var availability: String = "",
    var file_name: String = "",
    var views: String = "",
    var file_id: String = "",
    var subject: String = "",
    var file_url: String = "",
    var seekPosition: String = "",
    var subscriptionStatus: Boolean = false
)
