package com.ke.dawaati.app

import com.ke.dawaati.db.repo.AnalyticsRepository
import com.ke.dawaati.db.repo.ContentRepository
import com.ke.dawaati.db.repo.EvaluationsRepository
import com.ke.dawaati.db.repo.ResumeRepository
import com.ke.dawaati.db.repo.SchoolRepository
import com.ke.dawaati.db.repo.UserDetailsRepository
import com.ke.dawaati.uiux.MainViewModel
import com.ke.dawaati.uiux.authenticate.AuthenticationViewModel
import com.ke.dawaati.uiux.validated.HomeViewModel
import com.ke.dawaati.uiux.validated.content.ebooks.ContentEBooksViewModel
import com.ke.dawaati.uiux.validated.content.ebooks.details.ContentEBookDetailsViewModel
import com.ke.dawaati.uiux.validated.content.evaluations.ContentEvaluationViewModel
import com.ke.dawaati.uiux.validated.content.evaluations.attempt.EvaluationAttemptViewModel
import com.ke.dawaati.uiux.validated.content.evaluations.reports.EvaluationsReportViewModel
import com.ke.dawaati.uiux.validated.content.videos.ContentVideosViewModel
import com.ke.dawaati.uiux.validated.content.videos.details.ContentVideoDetailsVideoModel
import com.ke.dawaati.uiux.validated.subscribe.SubscribeViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val dawatiModule = module {
    // Repositories
    single { AnalyticsRepository(application = get()) }
    single { UserDetailsRepository(application = get()) }
    single { SchoolRepository(application = get()) }
    single { ContentRepository(application = get()) }
    single { EvaluationsRepository(application = get()) }
    single { ResumeRepository(application = get()) }

    viewModel {
        MainViewModel(
            analyticsRepository = get()
        )
    }

    viewModel {
        AuthenticationViewModel(
            dawatiAPI = get(),
            analyticsRepository = get(),
            userDetailsRepository = get(),
            schoolRepository = get(),
            configurationPrefs = get()
        )
    }

    viewModel {
        HomeViewModel(
            dawatiAPI = get(),
            analyticsRepository = get(),
            userDetailsRepository = get(),
            configurationPrefs = get(),
            resumeRepository = get()
        )
    }

    viewModel {
        SubscribeViewModel(
            dawatiAPI = get(),
            userDetailsRepository = get()
        )
    }

    viewModel {
        ContentVideosViewModel(
            dawatiAPI = get(),
            contentRepository = get(),
            configurationPrefs = get(),
            userDetailsRepository = get()
        )
    }

    viewModel {
        ContentEBooksViewModel(
            dawatiAPI = get(),
            contentRepository = get(),
            configurationPrefs = get()
        )
    }

    viewModel {
        ContentEvaluationViewModel(
            dawatiAPI = get(),
            evaluationsRepository = get(),
            configurationPrefs = get()
        )
    }

    viewModel {
        ContentVideoDetailsVideoModel(
            analyticsRepository = get(),
            resumeRepository = get(),
            contentRepository = get(),
            configurationPrefs = get(),
            userDetailsRepository = get()
        )
    }

    viewModel {
        ContentEBookDetailsViewModel(
            analyticsRepository = get(),
            contentRepository = get(),
            configurationPrefs = get()
        )
    }

    viewModel {
        EvaluationAttemptViewModel(
            dawatiAPI = get(),
            configurationPrefs = get(),
            userDetailsRepository = get()
        )
    }

    viewModel {
        EvaluationsReportViewModel(
            dawatiAPI = get(),
            configurationPrefs = get(),
            userDetailsRepository = get(),
            evaluationsRepository = get()
        )
    }
}
