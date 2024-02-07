package com.ke.dawaati.uiux.validated.content.videos.details

import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.ke.dawaati.R
import com.ke.dawaati.databinding.SubjectContentVideoDetailsExperimentalFragmentBinding
import com.ke.dawaati.db.model.PracticalsEntity
import com.ke.dawaati.preference.ConfigurationPrefs
import com.ke.dawaati.uiux.validated.content.videos.ContentPracticalVideosAdapter
import com.ke.dawaati.uiux.validated.content.videos.DisplayVideoSubTopics
import com.ke.dawaati.uiux.validated.content.videos.details.related.ContentVideosRelatedAdapter
import com.ke.dawaati.util.StringConstants.SECTIONED
import com.ke.dawaati.util.dp
import com.ke.dawaati.util.observeEvent
import com.ke.dawaati.util.toTitleCase
import com.ke.dawaati.util.viewBinding
import kotlinx.android.synthetic.main.subject_content_video_details_fragment.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class ContentVideoDetailsExperimentalFragment : Fragment(R.layout.subject_content_video_details_experimental_fragment) {

    private val viewModel: ContentVideoDetailsVideoModel by viewModel()

    private val binding by viewBinding(SubjectContentVideoDetailsExperimentalFragmentBinding::bind)

    private val prefs: ConfigurationPrefs by inject()

    private var bottomSheet: BottomSheetDialogFragment? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpOnClickListener()
        setupObservers()
        setupListAdapter()

        if (prefs.getVideoDirection() == SECTIONED)
            viewModel.loadAllPageContent(context = requireContext())
        else
            viewModel.loadPracticals(context = requireContext())
    }

    private fun setUpOnClickListener() {
        binding.apply {
            previousAttempts.setOnClickListener {
                viewModel.navigateToVideoSelection(context = requireContext())
            }
            videoPlayerFullscreen.setOnClickListener {
                if (activity?.requestedOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                    activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                } else {
                    activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                }
            }
        }
    }

    private fun setupObservers() {
        viewModel.uiState.observe(viewLifecycleOwner) {
            when (it) {
                is ContentVideoDetailsUIState.VideosContent -> {
                    renderVideoDetails(
                        fileName = toTitleCase(it.currentVideos.file_name, handleWhiteSpace = false)
                            ?: "",
                        caption = "${it.currentVideos.views} views",
                        fileURL = it.currentVideos.file_url + it.currentVideos.file_name.replace(
                            " ",
                            "%20"
                        )
                    )

                    renderRelatedVideos(
                        relatedVideos = it.displayRelatedVideos,
                        subject = it.subject,
                        level = it.level
                    )
                }
                is ContentVideoDetailsUIState.PracticalContent -> {
                    it.currentVideos?.let { practical ->
                        renderVideoDetails(
                            fileName = toTitleCase(practical.file_name, handleWhiteSpace = false)
                                ?: "",
                            caption = practical.availability,
                            fileURL = practical.file_url + practical.file_name.replace(" ", "%20")
                        )
                    }

                    relatedPracticals(
                        displayVideos = it.displayVideos,
                        subject = it.subject,
                        level = it.level,
                        subscriptionStatus = it.subscriptionStatus
                    )
                }
            }
        }

        viewModel.action.observeEvent(viewLifecycleOwner) {
            when (it) {
                is ContentVideoDetailsActions.BottomNavigate -> {
                    bottomSheet = it.bottomSheet
                    bottomSheet?.let { bsdf ->
                        bsdf.show(parentFragmentManager, bsdf.tag)
                    }
                }
            }
        }
    }

    private fun setupListAdapter() {
        binding.apply {
            val customAdapter = if (prefs.getVideoDirection() == SECTIONED)
                contentVideosRelatedAdapter
            else
                contentPracticalVideosAdapter
            relatedVideosList.adapter = customAdapter
        }
    }

    private fun relatedPracticals(
        displayVideos: List<PracticalsEntity>,
        subject: String,
        level: String,
        subscriptionStatus: Boolean
    ) {
        contentPracticalVideosAdapter.setPracticals(
            practicalsEntity = displayVideos,
            subject = subject,
            level = level,
            subscriptionStatus = subscriptionStatus
        )
    }

    private fun renderRelatedVideos(
        relatedVideos: List<DisplayVideoSubTopics>,
        subject: String,
        level: String
    ) {
        contentVideosRelatedAdapter.setRelatedVideos(
            videosEntity = relatedVideos,
            subject = subject,
            level = level
        )
    }

    private fun renderVideoDetails(fileName: String, caption: String, fileURL: String) {
        binding.apply {
            videoTitle.text = toTitleCase(fileName, handleWhiteSpace = false)
            videoViews.text = caption
            initializePlayer(fileURL)
        }
    }

    private var exoPlayer: ExoPlayer? = null

    private fun initializePlayer(url: String) {
        binding.apply {
            videoPlayer.requestFocus()
            exoPlayer = ExoPlayer.Builder(requireContext()).build()
            videoPlayer.player = exoPlayer
            exoPlayer?.let { player ->
                player.playWhenReady = true
                // Build the media items.
                val item = MediaItem.fromUri(url)
                // Add the media items to be played.
                player.addMediaItem(item)
                // Prepare the player.
                player.prepare()
                // Start the playback.
                player.play()
                player.addListener(object : Player.Listener {
                    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                        when (playbackState) {
                            Player.STATE_ENDED -> {
                                viewModel.deleteResumeVideo()
                                viewModel.closeVideoAnalytics()
                            }
                            else -> {}
                        }
                    }
                })
            }
        }
    }

    private val contentVideosRelatedAdapter by lazy {
        ContentVideosRelatedAdapter(
            selectedVideo = { displaySubTopics ->
                exoPlayer?.let {
                    it.release()
                    exoPlayer = null
                }
                initializePlayer(displaySubTopics.file_url + displaySubTopics.file_name.replace(" ", "%20"))

                viewModel.closeVideoAnalytics()
                viewModel.createVideoAnalytics(
                    context = requireContext(),
                    contentID = displaySubTopics.file_id
                )

                binding.apply {
                    videoTitle.text = toTitleCase(displaySubTopics.file_name, handleWhiteSpace = false)
                    videoViews.text = "${displaySubTopics.views} views"
                }
            },
            selectedPremiumVideo = {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        )
    }

    private val contentPracticalVideosAdapter by lazy {
        ContentPracticalVideosAdapter(
            selectedVideo = { displaySubTopics ->
                exoPlayer?.let {
                    it.release()
                    exoPlayer = null
                }
                initializePlayer(displaySubTopics.file_url + displaySubTopics.file_name.replace(" ", "%20"))

                viewModel.closeVideoAnalytics()
                viewModel.createVideoAnalytics(
                    context = requireContext(),
                    contentID = displaySubTopics.file_id
                )

                binding.apply {
                    videoTitle.text = toTitleCase(displaySubTopics.file_name, handleWhiteSpace = false)
                    videoTitleLandScape.text = toTitleCase(displaySubTopics.file_name, handleWhiteSpace = false)
                    videoViews.text = displaySubTopics.availability
                    videoViewsLandScape.text = displaySubTopics.availability
                }
            },
            selectedPremiumVideo = {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        )
    }

    override fun onPause() {
        super.onPause()
        viewModel.closeVideoAnalytics()
        exoPlayer?.pause()
    }

    override fun onDestroy() {
        viewModel.closeVideoAnalytics()
        exoPlayer?.let {
            it.release()
            exoPlayer = null
        }
        super.onDestroy()
        bottomSheet?.dismiss()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            binding.apply {
                val exoPlayerParams = videoPlayer.layoutParams
                exoPlayerParams.height = 200.dp
                videoPlayer.layoutParams = exoPlayerParams

                val exoPlayerConstraintParams = videoPlayer.layoutParams as ConstraintLayout.LayoutParams
                exoPlayerConstraintParams.bottomToBottom = -1
                videoPlayer.layoutParams = exoPlayerConstraintParams

                videoPlayerFullscreen.setImageDrawable(
                    VectorDrawableCompat.create(
                        resources,
                        R.drawable.ic_player_full_screen,
                        requireContext().theme
                    )
                )

                previousAttempts.isGone = true
                configurationToggle.isVisible = true
            }
        } else if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            binding.apply {
                val exoPlayerLayoutParams = videoPlayer.layoutParams
                exoPlayerLayoutParams.height = 0.dp
                videoPlayer.layoutParams = exoPlayerLayoutParams

                val exoPlayerConstraintParams = videoPlayer.layoutParams as ConstraintLayout.LayoutParams
                exoPlayerConstraintParams.bottomToBottom = motionLayout.id
                videoPlayer.layoutParams = exoPlayerConstraintParams

                videoPlayerFullscreen.setImageDrawable(
                    VectorDrawableCompat.create(
                        resources,
                        R.drawable.ic_player_full_screen_exit,
                        requireContext().theme
                    )
                )

                previousAttempts.isVisible = true
                configurationToggle.isGone = true
            }
        }
    }
}
