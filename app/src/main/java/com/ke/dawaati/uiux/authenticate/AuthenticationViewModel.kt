package com.ke.dawaati.uiux.authenticate

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavDirections
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.ke.dawaati.api.DawatiAPI
import com.ke.dawaati.db.model.AnalyticsEntity
import com.ke.dawaati.db.model.LevelsEntity
import com.ke.dawaati.db.model.SchoolEntity
import com.ke.dawaati.db.model.SubjectsEntity
import com.ke.dawaati.db.model.SubscriptionDetailsEntity
import com.ke.dawaati.db.model.SubscriptionTypesEntity
import com.ke.dawaati.db.model.UserDetailsEntity
import com.ke.dawaati.db.repo.AnalyticsRepository
import com.ke.dawaati.db.repo.SchoolRepository
import com.ke.dawaati.db.repo.UserDetailsRepository
import com.ke.dawaati.preference.ConfigurationPrefs
import com.ke.dawaati.uiux.authenticate.passwordRecovery.SignForgotDialogFragment
import com.ke.dawaati.uiux.authenticate.school.SchoolDialogFragment
import com.ke.dawaati.uiux.validated.level.LevelDialogFragment
import com.ke.dawaati.util.AnalyticConstants.ACCOUNT_ACTIVATED
import com.ke.dawaati.util.AnalyticConstants.ACCOUNT_CREATED
import com.ke.dawaati.util.AnalyticConstants.ACCOUNT_LOGIN
import com.ke.dawaati.util.AnalyticConstants.EMPTY
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
import org.koin.ext.isInt
import timber.log.Timber

class AuthenticationViewModel(
    private val dawatiAPI: DawatiAPI,
    private val analyticsRepository: AnalyticsRepository,
    private val userDetailsRepository: UserDetailsRepository,
    private val schoolRepository: SchoolRepository,
    private val configurationPrefs: ConfigurationPrefs
) : ViewModel() {

    private val _uiState = MutableLiveData<AuthenticationUIState>()
    val uiState: LiveData<AuthenticationUIState> = _uiState

    private val _action = MutableLiveData<Event<AuthenticationActions>>()
    val action: LiveData<Event<AuthenticationActions>> = _action

    private val exceptionHandler = CoroutineExceptionHandler { _, exception ->
        Timber.e(exception)
        _uiState.postValue(AuthenticationUIState.Error(message = exception.message!!))
    }

    private var selectedSchool: SchoolEntity? = null
    private var levelsEntity: LevelsEntity? = null

    fun signIn(email_phone: String, password: String, context: Context) {
        _uiState.postValue(AuthenticationUIState.Loading)
        viewModelScope.launch(exceptionHandler) {
            val response = withContext(Dispatchers.IO) {
                dawatiAPI.loginUser(
                    email_phone = email_phone,
                    password = password,
                    user_token = configurationPrefs.getToken()
                )
            }

            if (response.result != null && response.result.status.equals("true", ignoreCase = true)) {
                val userDetails = response.result.user_details

                userDetailsRepository.apply {
                    deleteUserDetails()
                    insertUserDetails(
                        userDetailsEntity = UserDetailsEntity(
                            user_id = userDetails!!.user_id,
                            username = userDetails.username,
                            email = userDetails.email,
                            mobile = userDetails.mobile,
                            subscription_status = userDetails.subscription_status,
                            user_type = userDetails.user_type,
                            about_me = userDetails.about_me,
                            name = userDetails.name,
                            level_name = userDetails.level_name,
                            prof_img = userDetails.prof_img,
                            file_url = userDetails.file_url
                        )
                    )
                }

                val subscriptionDetails = response.result.subscription_details[0]
                userDetailsRepository.apply {
                    deleteSubscriptionDetails()
                    insertSubscriptionDetails(
                        subscriptionDetailsEntity = SubscriptionDetailsEntity(
                            indexID = subscriptionDetails.index_id,
                            userID = subscriptionDetails.user_id,
                            mPesaTransactionID = subscriptionDetails.mpesa_transaction_id,
                            subscriptionType = subscriptionDetails.subscription_type,
                            status = subscriptionDetails.status,
                            startDate = subscriptionDetails.start_date,
                            expiry = subscriptionDetails.expiry
                        )
                    )
                }

                val subscriptionTypes = response.result.subscription_types
                userDetailsRepository.deleteSubscriptionTypes()
                subscriptionTypes.forEach { subTypes ->
                    userDetailsRepository.insertSubscriptionTypes(
                        subscriptionTypesEntity = SubscriptionTypesEntity(
                            subscriptionID = subTypes.subscription_id,
                            title = subTypes.title,
                            amount = subTypes.amount
                        )
                    )
                }

                val responseSubjects = withContext(Dispatchers.IO) {
                    dawatiAPI.fetchSubjects()
                }

                if (responseSubjects.status) {
                    val levels = responseSubjects.result!!.levels

                    userDetailsRepository.deleteLevels()
                    levels.forEach { level ->
                        userDetailsRepository.insertLevels(
                            levelsEntity = LevelsEntity(
                                level_code = level.level_code,
                                level_name = level.level_name
                            )
                        )
                    }

                    val subjects = responseSubjects.result.subjects

                    userDetailsRepository.deleteSubjects()
                    subjects.forEach { subject ->
                        userDetailsRepository.insertSubjects(
                            subjectsEntity = SubjectsEntity(
                                subjectID = subject.id,
                                name = subject.name,
                                logo = subject.logo
                            )
                        )
                    }
                }

                analyticsRepository.insertAnalytics(
                    analyticsEntity = AnalyticsEntity(
                        analyticsID = "ana${getTimeStamp()}",
                        startStamp = getCurrentTimeStamp(),
                        endStamp = getCurrentTimeStamp(),
                        contentType = ACCOUNT_LOGIN,
                        contentID = EMPTY,
                        internetType = getNetworkType(context = context),
                        phonetype = getModel()
                    )
                )

                configurationPrefs.setValidated(userValidated = true)
                _action.postValue(AuthenticationActions.Navigate(SignInFragmentDirections.toHomeFragment()).asEvent())
            } else {
                _uiState.postValue(AuthenticationUIState.Error(message = response.result?.message ?: ""))
            }
        }
    }

    fun signUp(
        full_name: String,
        email: String,
        phone: String,
        gender: String,
        level: String,
        school_name: String,
        password: String,
        confirmPassword: String,
        context: Context
    ) {
        _uiState.postValue(AuthenticationUIState.Loading)
        viewModelScope.launch(exceptionHandler) {
            val response = withContext(Dispatchers.IO) {
                dawatiAPI.registerUser(
                    full_name = full_name,
                    email = email,
                    phone = phone,
                    gender = gender,
                    level = level,
                    school_name = school_name,
                    password = password,
                    confirmPassword = confirmPassword,
                    user_token = configurationPrefs.getToken()
                )
            }

            analyticsRepository.insertAnalytics(
                analyticsEntity = AnalyticsEntity(
                    analyticsID = "ana${getTimeStamp()}",
                    startStamp = getCurrentTimeStamp(),
                    endStamp = getCurrentTimeStamp(),
                    contentType = ACCOUNT_CREATED,
                    contentID = EMPTY,
                    internetType = getNetworkType(context = context),
                    phonetype = getModel()
                )
            )

            response.result?.let {
                if (response.result.status.equals("true", ignoreCase = true))
                    _uiState.postValue(AuthenticationUIState.Success())
                else
                    _uiState.postValue(AuthenticationUIState.Error(message = response.result.message))
            } ?: _uiState.postValue(AuthenticationUIState.Error(message = "An unexpected error occurred. Please try again."))
        }
    }

    fun activateUser(email: String, code: String, context: Context) {
        _uiState.postValue(AuthenticationUIState.Loading)
        viewModelScope.launch(exceptionHandler) {
            val response = withContext(Dispatchers.IO) {
                dawatiAPI.activateUser(
                    email = email,
                    code = code
                )
            }

            analyticsRepository.insertAnalytics(
                analyticsEntity = AnalyticsEntity(
                    analyticsID = "ana${getTimeStamp()}",
                    startStamp = getCurrentTimeStamp(),
                    endStamp = getCurrentTimeStamp(),
                    contentType = ACCOUNT_ACTIVATED,
                    contentID = EMPTY,
                    internetType = getNetworkType(context = context),
                    phonetype = getModel()
                )
            )

            response.result?.let {
                if (response.result.status.equals("true", ignoreCase = true))
                    _uiState.postValue(AuthenticationUIState.Success())
                else
                    _uiState.postValue(AuthenticationUIState.Error(message = response.result.message))
            } ?: _uiState.postValue(AuthenticationUIState.Error(message = "An unexpected error occurred. Please try again."))
        }
    }

    fun navigateToSchoolSelection() {
        val bottomSheetFragment = SchoolDialogFragment(
            selectedVehicleCallBack = { selectedSchool ->
                this.selectedSchool = selectedSchool
                _uiState.postValue(AuthenticationUIState.SchoolChoice(school = selectedSchool))
            }
        )
        _action.postValue(AuthenticationActions.BottomNavigate(bottomSheetFragment).asEvent())
    }

    private var currentLevel = "0"

    fun navigateToSchoolLevelSelection() {
        val levels = mutableListOf<LevelsEntity>()
        levels.add(LevelsEntity(level_code = "level_001", level_name = "Form 1"))
        levels.add(LevelsEntity(level_code = "level_002", level_name = "Form 2"))
        levels.add(LevelsEntity(level_code = "level_003", level_name = "Form 3"))
        levels.add(LevelsEntity(level_code = "level_004", level_name = "Form 4"))
        val bottomSheetFragment = LevelDialogFragment(
            currentLevel = currentLevel,
            allLevels = levels,
            selectedLevelCallBack = { selectedLevel ->
                levelsEntity = selectedLevel
                configurationPrefs.setLevel(
                    levelID = selectedLevel.level_code,
                    levelName = selectedLevel.level_name
                )
                currentLevel = selectedLevel.level_code
                _uiState.postValue(AuthenticationUIState.SchoolLevelChoice(level = selectedLevel))
            }
        )
        _action.postValue(AuthenticationActions.BottomNavigate(bottomSheetFragment).asEvent())
    }

    fun loadSchools() {
        if (schoolRepository.countSchool() == 0) {
            _uiState.postValue(AuthenticationUIState.Loading)
            viewModelScope.launch(exceptionHandler) {
                withContext(Dispatchers.IO) {
                    val response = dawatiAPI.fetchSchools()

                    if (response.result != null && !response.result.schools.isNullOrEmpty()) {
                        val schools = response.result.schools
                        schools.forEach {
                            schoolRepository.insertSchool(
                                schoolEntity =
                                SchoolEntity(
                                    schoolID = it.school_code,
                                    schoolName = it.name
                                )
                            )
                        }

                        val schoolsList = schoolRepository.loadSchool().filterNot { it.schoolName.trim().isEmpty() || it.schoolName.length > 3 || !it.schoolName.trim().drop(2).isInt() }
                        _uiState.postValue(AuthenticationUIState.ListOfSchools(schoolsList = schoolsList))
                    }
                }
            }
        } else {
            val schoolsList = schoolRepository.loadSchool().filterNot { it.schoolName.trim().isEmpty() || it.schoolName.length < 3 || it.schoolName.trim().drop(2).isInt() }
            _uiState.postValue(AuthenticationUIState.ListOfSchools(schoolsList = schoolsList))
        }
    }

    fun navigateToPasswordReset() {
        val bottomSheetFragment = SignForgotDialogFragment()
        _action.postValue(AuthenticationActions.BottomNavigate(bottomSheetFragment).asEvent())
    }

    fun resetPassword(emailAddress: String) {
        _uiState.postValue(AuthenticationUIState.Loading)
        viewModelScope.launch(exceptionHandler) {
            val response = withContext(Dispatchers.IO) { dawatiAPI.resetPassword(email = emailAddress) }

            if (response.status)
                _uiState.value = AuthenticationUIState.Success(message = response.result)
            else
                _uiState.postValue(AuthenticationUIState.Error(message = response.result))
        }
    }
}

sealed class AuthenticationActions {
    data class Navigate(val destination: NavDirections) : AuthenticationActions()

    data class BottomNavigate(val bottomSheet: BottomSheetDialogFragment) :
        AuthenticationActions()
}

sealed class AuthenticationUIState {
    object Loading : AuthenticationUIState()

    data class Success(val message: String = "") : AuthenticationUIState()

    data class SchoolChoice(val school: SchoolEntity) : AuthenticationUIState()

    data class SchoolLevelChoice(val level: LevelsEntity) : AuthenticationUIState()

    data class ListOfSchools(val schoolsList: List<SchoolEntity>) : AuthenticationUIState()

    data class Error(val message: String) : AuthenticationUIState()
}
