package com.ke.dawaati.uiux.validated.content.videos.details

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.ke.dawaati.db.model.AnalyticsEntity
import com.ke.dawaati.db.model.PracticalsEntity
import com.ke.dawaati.db.model.ResumeVideosEntity
import com.ke.dawaati.db.model.SubTopicsEntity
import com.ke.dawaati.db.repo.AnalyticsRepository
import com.ke.dawaati.db.repo.ContentRepository
import com.ke.dawaati.db.repo.ResumeRepository
import com.ke.dawaati.db.repo.UserDetailsRepository
import com.ke.dawaati.preference.ConfigurationPrefs
import com.ke.dawaati.uiux.validated.content.videos.DisplayVideoSubTopics
import com.ke.dawaati.uiux.validated.content.videos.details.related.RelatedVideosDialogFragment
import com.ke.dawaati.util.AnalyticConstants
import com.ke.dawaati.util.Event
import com.ke.dawaati.util.asEvent
import com.ke.dawaati.util.getCurrentTimeStamp
import com.ke.dawaati.util.getModel
import com.ke.dawaati.util.getNetworkType
import com.ke.dawaati.util.getTimeStamp

class ContentVideoDetailsVideoModel(
    private val analyticsRepository: AnalyticsRepository,
    private val resumeRepository: ResumeRepository,
    private val contentRepository: ContentRepository,
    private val configurationPrefs: ConfigurationPrefs,
    userDetailsRepository: UserDetailsRepository
) : ViewModel() {

    private val _uiState = MutableLiveData<ContentVideoDetailsUIState>()
    val uiState: LiveData<ContentVideoDetailsUIState> = _uiState

    private val _action = MutableLiveData<Event<ContentVideoDetailsActions>>()
    val action: LiveData<Event<ContentVideoDetailsActions>> = _action

    private var subscriptionStatus = (userDetailsRepository.loadUserDetails()?.subscription_status ?: "").equals("active", ignoreCase = true)

    private var currentVideo: SubTopicsEntity? = null
    private var displayRelatedVideos = mutableListOf<DisplayVideoSubTopics>()
    private var currentPracticalVideo: PracticalsEntity? = null
    private var displayPracticalVideos = mutableListOf<PracticalsEntity>()

    private var analyticsID: String = ""

    fun createVideoAnalytics(context: Context, contentID: String) {
        analyticsID = "ana${getTimeStamp()}"

        analyticsRepository.insertAnalytics(
            analyticsEntity = AnalyticsEntity(
                analyticsID = analyticsID,
                startStamp = getCurrentTimeStamp(),
                endStamp = AnalyticConstants.EMPTY,
                contentType = AnalyticConstants.VIDEOS,
                contentID = contentID,
                internetType = getNetworkType(context = context),
                phonetype = getModel()
            )
        )
    }

    fun loadPracticals(context: Context) {
        currentPracticalVideo = contentRepository.loadPracticalsByFileID(
            file_id = configurationPrefs.getVideos()
        )

        createVideoAnalytics(context = context, contentID = configurationPrefs.getVideos())

        contentRepository.loadPracticals(
            subject_id = currentPracticalVideo?.subject_id ?: configurationPrefs.getSubject().first ?: "",
            level_id = currentPracticalVideo?.level_id ?: configurationPrefs.getLevel().first ?: ""
        ).map { displayPracticalVideos.add(it) }

        _uiState.value = ContentVideoDetailsUIState.PracticalContent(
            currentVideos = currentPracticalVideo,
            displayVideos = displayPracticalVideos,
            subject = configurationPrefs.getSubject().second ?: "",
            level = configurationPrefs.getLevel().second ?: "",
            subscriptionStatus = subscriptionStatus
        )
    }

    fun loadAllPageContent(context: Context) {
        currentVideo = contentRepository.loadSubTopicsByFileID(
            file_id = configurationPrefs.getVideos()
        )

        createVideoAnalytics(context = context, contentID = configurationPrefs.getVideos())

        if (resumeRepository.countExistingResumeVideos(file_id = configurationPrefs.getVideos()) == 0)
            updateResumeVideos()

        currentVideo?.let { video ->
            val videoSubTopics = contentRepository
                .loadSubTopics(
                    topicCategory = video.topicCategory,
                    subject_id = video.subject_id,
                    level_id = video.level_id
                )

            videoSubTopics.filter {
                it.topicID == video.topicID
            }.map {
                displayRelatedVideos.add(
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

            _uiState.value = ContentVideoDetailsUIState.VideosContent(
                currentVideos = video,
                displayRelatedVideos = displayRelatedVideos,
                subject = configurationPrefs.getSubject().second ?: "",
                level = configurationPrefs.getLevel().second ?: ""
            )
        }
    }

    private fun updateResumeVideos() {
        val currentVideo = contentRepository.loadSubTopicsByFileID(
            file_id = configurationPrefs.getVideos() ?: ""
        )

        if (resumeRepository.countResumeVideos() >= 5)
            resumeRepository.deleteLastResumeVideos()

        currentVideo?.let {
            resumeRepository.insertResumeVideos(
                resumeVideosEntity = ResumeVideosEntity(
                    indexID = it.indexID,
                    subtopicID = it.indexID,
                    name = it.name,
                    availability = it.availability,
                    file_name = it.file_name,
                    views = it.views,
                    file_id = it.file_id,
                    subject = it.subject,
                    file_url = it.file_url,
                    seekPosition = ""
                )
            )
        }
    }

    fun deleteResumeVideo() {
        resumeRepository.deleteResumeVideoByFileID(
            file_id = configurationPrefs.getVideos()
        )
    }

    fun navigateToVideoSelection(context: Context) {
        val bottomSheetFragment = RelatedVideosDialogFragment(
            currentVideo = currentVideo,
            displayRelatedVideos = displayRelatedVideos,
            currentPracticalVideo = currentPracticalVideo,
            displayPracticalVideos = displayPracticalVideos,
            subscriptionStatus = subscriptionStatus,
            videoCallBack = { loadPracticals(context = context) },
            practicalCallBack = { loadAllPageContent(context = context) }
        )
        _action.postValue(ContentVideoDetailsActions.BottomNavigate(bottomSheetFragment).asEvent())
    }

    fun closeVideoAnalytics() {
        analyticsRepository.updateAnalytics(
            endStamp = getCurrentTimeStamp(),
            analyticsID = analyticsID
        )
    }
}

sealed class ContentVideoDetailsActions {
    data class BottomNavigate(val bottomSheet: BottomSheetDialogFragment) :
        ContentVideoDetailsActions()
}

sealed class ContentVideoDetailsUIState {
    data class PracticalContent(
        val currentVideos: PracticalsEntity?,
        val displayVideos: List<PracticalsEntity>,
        val subject: String,
        val level: String,
        val subscriptionStatus: Boolean
    ) : ContentVideoDetailsUIState()

    data class VideosContent(
        val currentVideos: SubTopicsEntity,
        val displayRelatedVideos: List<DisplayVideoSubTopics>,
        val subject: String,
        val level: String
    ) : ContentVideoDetailsUIState()
}
