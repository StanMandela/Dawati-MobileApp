package com.ke.dawaati.uiux.validated

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavDirections
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.ke.dawaati.R
import com.ke.dawaati.api.DawatiAPI
import com.ke.dawaati.db.model.AnalyticsEntity
import com.ke.dawaati.db.model.SubjectsEntity
import com.ke.dawaati.db.model.UserDetailsEntity
import com.ke.dawaati.db.repo.AnalyticsRepository
import com.ke.dawaati.db.repo.ResumeRepository
import com.ke.dawaati.db.repo.UserDetailsRepository
import com.ke.dawaati.preference.ConfigurationPrefs
import com.ke.dawaati.uiux.validated.content.videos.DisplayVideoSubTopics
import com.ke.dawaati.uiux.validated.level.LevelDialogFragment
import com.ke.dawaati.util.AnalyticConstants
import com.ke.dawaati.util.Event
import com.ke.dawaati.util.asEvent
import com.ke.dawaati.util.getCurrentTimeStamp
import com.ke.dawaati.util.getModel
import com.ke.dawaati.util.getNetworkType
import com.ke.dawaati.util.getTimeStamp
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import timber.log.Timber

class HomeViewModel(
    private val dawatiAPI: DawatiAPI,
    private val analyticsRepository: AnalyticsRepository,
    private val userDetailsRepository: UserDetailsRepository,
    private val configurationPrefs: ConfigurationPrefs,
    private val resumeRepository: ResumeRepository
) : ViewModel() {

    private val _uiState = MutableLiveData<HomeUIState>()
    val uiState: LiveData<HomeUIState> = _uiState

    private val _action = MutableLiveData<Event<HomeActions>>()
    val action: LiveData<Event<HomeActions>> = _action

    private val exceptionHandler = CoroutineExceptionHandler { _, exception ->
        Timber.e(exception)
    }

    fun syncAnalytics() {
        viewModelScope.launch(exceptionHandler) {
            val analytics = analyticsRepository.syncAnalytics()

            val parentObject = JSONObject()

            try {
                parentObject.put("analytics_count", analytics.size.toString())
                val analyticsArray = JSONArray()
                analytics.forEach { analytic ->
                    val analyticObj = JSONObject()
                    analyticObj.put("analytics_id", analytic.analyticsID)
                    analyticObj.put("startStamp", analytic.startStamp)
                    analyticObj.put("endStamp", analytic.endStamp)
                    analyticObj.put("contentType", analytic.contentType)
                    analyticObj.put("contentID", analytic.contentID)
                    analyticObj.put("internetType", analytic.internetType)
                    analyticObj.put("phonetype", analytic.phonetype)
                    analyticsArray.put(analyticObj)
                }
                parentObject.put("analytics_data", analyticsArray)
            } catch (e: JSONException) {
                e.printStackTrace()
            }

            val responseSubjectExams = withContext(Dispatchers.IO) {
                dawatiAPI.submitAnalytics(
                    user_id = userDetailsRepository.loadUserDetails()?.user_id ?: "",
                    analytics = parentObject.toString()
                )
            }
        }
    }

    fun loadAnalytics() {
        val analytics = analyticsRepository.syncAnalytics()
        println(analytics)
    }

    fun loadAccountDetails(): UserDetailsEntity? {
        return userDetailsRepository.loadUserDetails()
    }

    fun getSubscriptionStatus() = (userDetailsRepository.loadUserDetails()?.subscription_status ?: "").equals("active", ignoreCase = true)

    fun loadSubjects() {
        val subjects = userDetailsRepository.loadSubjects()

        val resumeVideos = resumeRepository.loadResumeVideos()
        val relatedSubtopics = mutableListOf<DisplayVideoSubTopics>()
        resumeVideos.forEach {
            relatedSubtopics.add(
                DisplayVideoSubTopics(
                    id = it.id,
                    topicID = "",
                    indexID = it.indexID,
                    subTopicID = "",
                    name = it.name,
                    availability = it.availability,
                    file_name = it.file_name,
                    views = it.views,
                    file_id = it.file_id,
                    subject = it.subject,
                    file_url = it.file_url,
                    seekPosition = it.seekPosition
                )
            )
        }

        val mutableSubjects = mutableListOf<SubjectsEntity>().also {
            it.addAll(subjects)
            it.add(
                SubjectsEntity(
                    subjectID = "-1",
                    name = "Take Test",
                    logo = ""
                )
            )
            it.add(
                SubjectsEntity(
                    subjectID = "-2",
                    name = "WhatsApp",
                    logo = ""
                )
            )
        }

        _uiState.postValue(
            HomeUIState.Subjects(
                displayVideos = relatedSubtopics,
                subjects = mutableSubjects
            )
        )
    }

    fun loadFormSelect() {
        val levels = userDetailsRepository.loadLevels()
        val bottomSheetFragment = LevelDialogFragment(
            currentLevel = configurationPrefs.getLevel().first!!,
            allLevels = levels,
            selectedLevelCallBack = { selectedLevel ->
                configurationPrefs.setLevel(
                    levelID = selectedLevel.level_code,
                    levelName = selectedLevel.level_name
                )
                _uiState.postValue(HomeUIState.SelectedLevel(level = selectedLevel.level_name))
            }
        )
        _action.postValue(HomeActions.BottomNavigate(bottomSheetFragment).asEvent())
    }

    fun viewSubjectContent(subjectsEntity: SubjectsEntity) {
        configurationPrefs.setSubject(
            subjectID = subjectsEntity.subjectID,
            subjectName = subjectsEntity.name
        )
        _action.postValue(
            HomeActions
                .Navigate(HomeFragmentDirections.toSubjectContentFragment())
                .asEvent()
        )
    }

    fun viewVideoContent(video: DisplayVideoSubTopics, context: Context) {
        configurationPrefs.setSubject(
            subjectID = video.file_id,
            subjectName = video.name
        )

        configurationPrefs.setVideos(
            videoID = video.file_id
        )

        analyticsRepository.insertAnalytics(
            analyticsEntity = AnalyticsEntity(
                analyticsID = "ana${getTimeStamp()}",
                startStamp = getCurrentTimeStamp(),
                endStamp = AnalyticConstants.EMPTY,
                contentType = AnalyticConstants.VIDEOS,
                contentID = video.file_id,
                internetType = getNetworkType(context = context),
                phonetype = getModel()
            )
        )

        _action.postValue(
            HomeActions
                .Navigate(HomeFragmentDirections.toContentVideoDetailsFragment())
                .asEvent()
        )
    }

    fun shareApp(activity: Activity) {
        analyticsRepository.insertAnalytics(
            analyticsEntity = AnalyticsEntity(
                analyticsID = "ana${getTimeStamp()}",
                startStamp = getCurrentTimeStamp(),
                endStamp = getCurrentTimeStamp(),
                contentType = AnalyticConstants.SHARE_APP,
                contentID = AnalyticConstants.EMPTY,
                internetType = getNetworkType(context = activity.applicationContext),
                phonetype = getModel()
            )
        )

        val googlePlayUrl = "https://play.google.com/store/apps/details?id="
        val msg = activity.getString(R.string.share_message) + " "

        val shareIntent = Intent()
        shareIntent.action = Intent.ACTION_SEND
        shareIntent.putExtra(Intent.EXTRA_TEXT, msg + googlePlayUrl + activity.packageName)
        shareIntent.type = "text/plain"
        activity.startActivity(Intent.createChooser(shareIntent, "Share..."))
    }

    fun rateApp(activity: Activity) {
        analyticsRepository.insertAnalytics(
            analyticsEntity = AnalyticsEntity(
                analyticsID = "ana${getTimeStamp()}",
                startStamp = getCurrentTimeStamp(),
                endStamp = getCurrentTimeStamp(),
                contentType = AnalyticConstants.REVIEW_APP,
                contentID = AnalyticConstants.EMPTY,
                internetType = getNetworkType(context = activity.applicationContext),
                phonetype = getModel()
            )
        )

        activity.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=${activity.packageName}")))
    }

    fun openCareers() {
        _action.postValue(
            HomeActions
                .Navigate(HomeFragmentDirections.toCareerFragment())
                .asEvent()
        )
    }

    fun openSubscribe() {
        _action.postValue(
            HomeActions
                .Navigate(HomeFragmentDirections.toSubscribeFragment())
                .asEvent()
        )
    }

    fun logout() {
        _action.postValue(
            HomeActions
                .Navigate(HomeFragmentDirections.logout())
                .asEvent()
        )
    }
}

sealed class HomeActions {
    data class Navigate(val destination: NavDirections) : HomeActions()

    data class BottomNavigate(val bottomSheet: BottomSheetDialogFragment) :
        HomeActions()
}

sealed class HomeUIState {
    object Loading : HomeUIState()

    data class Subjects(
        val displayVideos: List<DisplayVideoSubTopics>,
        val subjects: List<SubjectsEntity>
    ) : HomeUIState()

    data class SelectedLevel(val level: String) : HomeUIState()

    data class Error(val statusCode: Boolean, val message: String) : HomeUIState()
}
