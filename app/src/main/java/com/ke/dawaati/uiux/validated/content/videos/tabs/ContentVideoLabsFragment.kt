package com.ke.dawaati.uiux.validated.content.videos.tabs

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.ke.dawaati.R
import com.ke.dawaati.databinding.SubjectContentVideosLabsFragmentBinding
import com.ke.dawaati.db.model.PracticalsEntity
import com.ke.dawaati.uiux.validated.content.videos.ContentPracticalVideosAdapter
import com.ke.dawaati.uiux.validated.content.videos.ContentVideosActions
import com.ke.dawaati.uiux.validated.content.videos.ContentVideosUIState
import com.ke.dawaati.uiux.validated.content.videos.ContentVideosViewModel
import com.ke.dawaati.util.StringConstants.NORMAL
import com.ke.dawaati.util.observeEvent
import com.ke.dawaati.util.viewBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class ContentVideoLabsFragment : Fragment(R.layout.subject_content_videos_labs_fragment) {

    private val viewModel: ContentVideosViewModel by viewModel()

    private val binding by viewBinding(SubjectContentVideosLabsFragmentBinding::bind)

    private val mainNavController by lazy { requireActivity().findNavController(R.id.navHostFragment) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupObservers()
        setupListAdapter()

        viewModel.loadLabPracticalVideos()
    }

    private fun setupObservers() {
        viewModel.uiState.observe(viewLifecycleOwner) {
            when (it) {
                ContentVideosUIState.Loading -> renderLoader()
                is ContentVideosUIState.PracticalContent -> {
                    renderVideos(
                        videosEntity = it.displayVideos,
                        subject = it.subject,
                        level = it.level,
                        subscriptionStatus = it.subscriptionStatus
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
            labList.adapter = contentPracticalVideosAdapter
        }
    }

    private fun renderLoader() {
        binding.apply {
            labList.isGone = true
            labEmptyState.isGone = true
            labLoadingLargeState.isVisible = true
            labLoadingBannerState.isGone = true
        }
    }

    private fun renderLoaderBanner() {
        binding.apply {
            labList.isVisible = true
            labEmptyState.isGone = true
            labLoadingLargeState.isGone = true
            labLoadingBannerState.isVisible = true
        }
    }

    private fun renderVideos(
        videosEntity: List<PracticalsEntity>,
        subject: String,
        level: String,
        subscriptionStatus: Boolean
    ) {
        binding.apply {
            labList.isVisible = videosEntity.isNotEmpty()
            labEmptyState.apply {
                isVisible = videosEntity.isEmpty()
                setEmptySubTitle(subTitle = "There are no lab videos for ${viewModel.getSubject()}, ${viewModel.getLevel()}")
            }
            labLoadingLargeState.isGone = true
            labLoadingBannerState.isGone = true
            contentPracticalVideosAdapter.setPracticals(
                practicalsEntity = videosEntity,
                subject = subject,
                level = level,
                subscriptionStatus = subscriptionStatus
            )
        }
    }

    private val contentPracticalVideosAdapter by lazy {
        ContentPracticalVideosAdapter(
            selectedVideo = { displaySubTopics ->
                viewModel.viewVideo(file_id = displaySubTopics.file_id, direction = NORMAL)
            },
            selectedPremiumVideo = {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        )
    }
}
