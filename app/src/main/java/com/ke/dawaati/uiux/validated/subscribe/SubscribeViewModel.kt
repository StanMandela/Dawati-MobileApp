package com.ke.dawaati.uiux.validated.subscribe

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ke.dawaati.api.DawatiAPI
import com.ke.dawaati.db.model.SubscriptionTypesEntity
import com.ke.dawaati.db.repo.UserDetailsRepository
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

class SubscribeViewModel(
    private val dawatiAPI: DawatiAPI,
    private val userDetailsRepository: UserDetailsRepository
) : ViewModel() {

    private val _uiState = MutableLiveData<SubscribeUIState>()
    val uiState: LiveData<SubscribeUIState> = _uiState

    private val exceptionHandler = CoroutineExceptionHandler { _, exception ->
        Timber.e(exception)
        _uiState.postValue(SubscribeUIState.Error(message = exception.message!!))
    }

    init {
        prepareSubscriptionPage()
    }

    private fun prepareSubscriptionPage() {
        val subscriptionItems = userDetailsRepository.loadSubscriptionTypes()

        _uiState.postValue(
            SubscribeUIState.SubscriptionOptions(
                subscriptionItems = subscriptionItems,
                phoneNumber = userDetailsRepository.loadUserDetails()?.mobile ?: ""
            )
        )
    }

    fun subscribeToDawati(phoneNumber: String, subscriptionType: String) {
        _uiState.postValue(SubscribeUIState.Loading)
        viewModelScope.launch(exceptionHandler) {
            val response = withContext(Dispatchers.IO) {
                dawatiAPI.subscribePremium(
                    mobile = phoneNumber,
                    subscription_type = subscriptionType,
                    user_id = userDetailsRepository.loadUserDetails()?.user_id ?: ""
                )
            }

            response.result?.let {
                if (response.result.status.equals("true", ignoreCase = true))
                    _uiState.postValue(SubscribeUIState.Success(message = response.result.message))
                else
                    _uiState.postValue(SubscribeUIState.Error(message = response.result.message))
            } ?: _uiState.postValue(SubscribeUIState.Error(message = "An unexpected error occurred. Please try again."))
        }
    }
}

sealed class SubscribeUIState {
    object Loading : SubscribeUIState()

    data class Success(val message: String) : SubscribeUIState()

    data class SubscriptionOptions(
        val subscriptionItems: List<SubscriptionTypesEntity>,
        val phoneNumber: String
    ) : SubscribeUIState()

    data class Error(val message: String) : SubscribeUIState()
}
