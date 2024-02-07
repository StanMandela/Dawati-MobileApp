package com.ke.dawaati.uiux.validated.content.ebooks

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavDirections
import com.ke.dawaati.api.DawatiAPI
import com.ke.dawaati.db.model.EBooksEntity
import com.ke.dawaati.db.repo.ContentRepository
import com.ke.dawaati.preference.ConfigurationPrefs
import com.ke.dawaati.uiux.validated.content.SubjectContentFragmentDirections
import com.ke.dawaati.util.Event
import com.ke.dawaati.util.asEvent
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

class ContentEBooksViewModel(
    private val dawatiAPI: DawatiAPI,
    private val contentRepository: ContentRepository,
    private val configurationPrefs: ConfigurationPrefs
) : ViewModel() {

    private val _uiState = MutableLiveData<ContentEBooksUIState>()
    val uiState: LiveData<ContentEBooksUIState> = _uiState

    private val _action = MutableLiveData<Event<ContentEBooksActions>>()
    val action: LiveData<Event<ContentEBooksActions>> = _action

    private val exceptionHandler = CoroutineExceptionHandler { _, exception ->
        Timber.e(exception)
        _uiState.postValue(ContentEBooksUIState.Error(statusCode = false, message = exception.message!!))
    }

    private var classEBooksRequested = false
    private var revisionEBooksRequested = false

    fun getSubject() = configurationPrefs.getSubject().second!!
    fun getLevel() = configurationPrefs.getLevel().second!!

    fun viewEBooks(displayEBooks: DisplayEBooks) {
        configurationPrefs.setEBooks(eBookID = displayEBooks.id.toString())
        _action.postValue(
            ContentEBooksActions
                .Navigate(
                    SubjectContentFragmentDirections.toContentEBooksFragment()
                )
                .asEvent()
        )
    }

    fun loadClassBooks() {
        val eBooks = contentRepository
            .loadEBooks(
                ebook_category = "Class",
                subject_id = configurationPrefs.getSubject().first!!,
                level_id = configurationPrefs.getLevel().first!!
            )

        if (eBooks.isEmpty() && !classEBooksRequested) {
            _uiState.postValue(ContentEBooksUIState.Loading)
            fetchClassBooks()
        } else {
            val displayEBooks = mutableListOf<DisplayEBooks>()
            eBooks.forEach { eBook ->
                displayEBooks.add(eBook.toEBookDisplay())
            }
            _uiState.postValue(
                ContentEBooksUIState
                    .EBooksContent(
                        eBooks = displayEBooks,
                        updateRequest = classEBooksRequested,
                        subject = configurationPrefs.getSubject().second!!,
                        level = configurationPrefs.getLevel().second!!
                    )
            )
            if (!classEBooksRequested) {
                fetchClassBooks()
            }
        }
    }

    private fun fetchClassBooks() {
        classEBooksRequested = true
        viewModelScope.launch(exceptionHandler) {
            val responseClassBooks = withContext(Dispatchers.IO) {
                dawatiAPI.fetchClassBooks(
                    level = configurationPrefs.getLevel().first!!,
                    subject = configurationPrefs.getSubject().second!!
                )
            }

            if (responseClassBooks.result!!.status.equals("true", ignoreCase = true)) {
                val books = responseClassBooks.result.books
                if (books.isNotEmpty()) {
                    books.forEach { booksTopics ->
                        contentRepository.deleteEBooks(
                            file_id = booksTopics.file_id
                        )
                        contentRepository.insertEBooks(
                            eBooksEntity = EBooksEntity(
                                file_id = booksTopics.file_id,
                                file_name = booksTopics.file_name,
                                multimedia_series = booksTopics.multimedia_series,
                                subject = booksTopics.subject,
                                file_url = booksTopics.file_url,
                                ebook_category = "Class",
                                level_id = configurationPrefs.getLevel().first!!,
                                level_name = configurationPrefs.getLevel().second!!,
                                subject_id = configurationPrefs.getSubject().first!!,
                                subject_name = configurationPrefs.getSubject().second!!
                            )
                        )
                    }
                }
            }

            loadClassBooks()
        }
    }

    fun loadRevisionBooks() {
        val eBooks = contentRepository
            .loadEBooks(
                ebook_category = "Revision",
                subject_id = configurationPrefs.getSubject().first!!,
                level_id = configurationPrefs.getLevel().first!!
            )

        if (eBooks.isEmpty() && !revisionEBooksRequested) {
            _uiState.postValue(ContentEBooksUIState.Loading)
            fetchRevisionBooks()
        } else {
            val displayEBooks = mutableListOf<DisplayEBooks>()
            eBooks.forEach { eBook ->
                displayEBooks.add(eBook.toEBookDisplay())
            }
            _uiState.postValue(
                ContentEBooksUIState
                    .EBooksContent(
                        eBooks = displayEBooks,
                        updateRequest = revisionEBooksRequested,
                        subject = configurationPrefs.getSubject().second!!,
                        level = configurationPrefs.getLevel().second!!
                    )
            )
            if (!revisionEBooksRequested) {
                fetchRevisionBooks()
            }
        }
    }

    private fun fetchRevisionBooks() {
        revisionEBooksRequested = true
        viewModelScope.launch(exceptionHandler) {
            val responseRevisionBooks = withContext(Dispatchers.IO) {
                dawatiAPI.fetchRevisionBooks(
                    level = configurationPrefs.getLevel().first!!,
                    subject = configurationPrefs.getSubject().second!!
                )
            }

            if (responseRevisionBooks.result!!.status.equals("true", ignoreCase = true)) {
                val books = responseRevisionBooks.result.books
                if (books.isNotEmpty()) {
                    books.forEach { booksTopics ->
                        contentRepository.deleteEBooks(
                            file_id = booksTopics.file_id
                        )
                        contentRepository.insertEBooks(
                            eBooksEntity = EBooksEntity(
                                file_id = booksTopics.file_id,
                                file_name = booksTopics.file_name,
                                multimedia_series = booksTopics.multimedia_series,
                                subject = booksTopics.subject,
                                file_url = booksTopics.file_url,
                                topic_id = booksTopics.topicID,
                                sub_topic_id = booksTopics.subtopicID,
                                file_type = booksTopics.file_type,
                                availability = booksTopics.availability,
                                ebook_category = "Revision",
                                level_id = configurationPrefs.getLevel().first!!,
                                level_name = configurationPrefs.getLevel().second!!,
                                subject_id = configurationPrefs.getSubject().first!!,
                                subject_name = configurationPrefs.getSubject().second!!
                            )
                        )
                    }
                }
            }
            loadRevisionBooks()
        }
    }
}

sealed class ContentEBooksActions {
    data class Navigate(val destination: NavDirections) : ContentEBooksActions()
}

sealed class ContentEBooksUIState {
    object Loading : ContentEBooksUIState()

    data class EBooksContent(
        val eBooks: List<DisplayEBooks>,
        val updateRequest: Boolean,
        val subject: String,
        val level: String
    ) : ContentEBooksUIState()

    data class Error(val statusCode: Boolean, val message: String) : ContentEBooksUIState()
}

data class DisplayEBooks(
    var id: Int = 0,
    var file_id: String = "",
    var file_name: String = "",
    var multimedia_series: String = "",
    var subject: String = "",
    var file_url: String = "",
    var ebook_category: String = ""
)

fun EBooksEntity.toEBookDisplay() = DisplayEBooks(
    id = id,
    file_id = file_id,
    file_name = file_name,
    multimedia_series = multimedia_series,
    subject = subject,
    file_url = file_url,
    ebook_category = ebook_category
)
