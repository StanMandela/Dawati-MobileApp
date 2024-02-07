package com.ke.dawaati.uiux.validated.content.videos.tabs

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.ke.dawaati.R
import com.ke.dawaati.databinding.SubjectContentVideosExamsFragmentBinding
import com.ke.dawaati.uiux.validated.content.videos.ContentVideosActions
import com.ke.dawaati.uiux.validated.content.videos.ContentVideosAdapter
import com.ke.dawaati.uiux.validated.content.videos.ContentVideosUIState
import com.ke.dawaati.uiux.validated.content.videos.ContentVideosViewModel
import com.ke.dawaati.uiux.validated.content.videos.DisplayVideoTopics
import com.ke.dawaati.util.observeEvent
import com.ke.dawaati.util.viewBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class ContentVideoExamsFragment : Fragment(R.layout.subject_content_videos_exams_fragment) {

    private val viewModel: ContentVideosViewModel by viewModel()

    private val binding by viewBinding(SubjectContentVideosExamsFragmentBinding::bind)

    private val mainNavController by lazy { requireActivity().findNavController(R.id.navHostFragment) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupObservers()
        setupListAdapter()

        viewModel.loadExamVideos()
    }

    private fun setupObservers() {
        viewModel.uiState.observe(viewLifecycleOwner) {
            when (it) {
                ContentVideosUIState.Loading -> renderLoader()
                is ContentVideosUIState.VideosContent -> {
                    renderVideos(
                        videosEntity = it.displayVideos,
                        subject = it.subject,
                        level = it.level
                    )
                    if (!it.updateRequest) {
                        renderLoaderBanner()
                    }
                }
                is ContentVideosUIState.Error -> {}
            }
        }

        viewModel.action.observeEvent(viewLifecycleOwner) {
            when (it) {
                is ContentVideosActions.Navigate -> {
                    mainNavController.navigate(it.destination)
                }
            }
        }
    }

    private fun setupListAdapter() {
        binding.apply {
            examVideosList.adapter = contentVideosAdapter
        }
    }

    private fun renderLoader() {
        binding.apply {
            examVideosList.isGone = true
            examVideosEmptyState.isGone = true
            examLoadingLargeState.isVisible = true
            examLoadingBannerState.isGone = true
        }
    }

    private fun renderLoaderBanner() {
        binding.apply {
            examVideosList.isVisible = true
            examVideosEmptyState.isGone = true
            examLoadingLargeState.isGone = true
            examLoadingBannerState.isVisible = true
        }
    }

    private fun renderVideos(
        videosEntity: List<DisplayVideoTopics>,
        subject: String,
        level: String
    ) {
        binding.apply {
            examVideosList.isVisible = videosEntity.isNotEmpty()
            examVideosEmptyState.apply {
                isVisible = videosEntity.isEmpty()
                setEmptySubTitle(subTitle = "There are no exam videos for ${viewModel.getSubject()}, ${viewModel.getLevel()}")
            }
            examLoadingLargeState.isGone = true
            examLoadingBannerState.isGone = true
            contentVideosAdapter.setVideos(
                videosEntity = videosEntity,
                subject = subject,
                level = level
            )
        }
    }

    private val contentVideosAdapter: ContentVideosAdapter by lazy {
        ContentVideosAdapter(
            selectedVideo = { displaySubTopics ->
                viewModel.viewVideo(file_id = displaySubTopics.file_id)
            },
            selectedPremiumVideo = {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        )
    }
}
