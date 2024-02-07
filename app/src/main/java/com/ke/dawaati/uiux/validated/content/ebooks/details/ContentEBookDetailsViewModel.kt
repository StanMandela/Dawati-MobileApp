package com.ke.dawaati.uiux.validated.content.ebooks.details

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ke.dawaati.db.model.AnalyticsEntity
import com.ke.dawaati.db.model.EBooksEntity
import com.ke.dawaati.db.repo.AnalyticsRepository
import com.ke.dawaati.db.repo.ContentRepository
import com.ke.dawaati.preference.ConfigurationPrefs
import com.ke.dawaati.util.AnalyticConstants
import com.ke.dawaati.util.getCurrentTimeStamp
import com.ke.dawaati.util.getModel
import com.ke.dawaati.util.getNetworkType
import com.ke.dawaati.util.getTimeStamp

class ContentEBookDetailsViewModel(
    private val analyticsRepository: AnalyticsRepository,
    private val contentRepository: ContentRepository,
    private val configurationPrefs: ConfigurationPrefs
) : ViewModel() {

    private val _uiState = MutableLiveData<ContentEBookDetailsUIState>()
    val uiState: LiveData<ContentEBookDetailsUIState> = _uiState

    private var analyticsID: String = ""

    fun loadAllEBookContent(context: Context) {
        configurationPrefs.getEBooks()?.toInt()?.let {
            val currentEBook = contentRepository.loadEBooksByID(id = it)

            analyticsID = "ana${getTimeStamp()}"

            analyticsRepository.insertAnalytics(
                analyticsEntity = AnalyticsEntity(
                    analyticsID = analyticsID,
                    startStamp = getCurrentTimeStamp(),
                    endStamp = AnalyticConstants.EMPTY,
                    contentType = AnalyticConstants.EBOOKS,
                    contentID = currentEBook.file_id,
                    internetType = getNetworkType(context = context),
                    phonetype = getModel()
                )
            )

            _uiState.postValue(
                ContentEBookDetailsUIState.EBooksContent(
                    currentEBooks = currentEBook
                )
            )
        }
    }

    fun closeEbookAnalytics() {
        analyticsRepository.updateAnalytics(
            endStamp = getCurrentTimeStamp(),
            analyticsID = analyticsID
        )
    }
}

sealed class ContentEBookDetailsUIState {
    data class EBooksContent(
        val currentEBooks: EBooksEntity
    ) : ContentEBookDetailsUIState()
}
