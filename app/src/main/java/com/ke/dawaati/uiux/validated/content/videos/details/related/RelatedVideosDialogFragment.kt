package com.ke.dawaati.uiux.validated.content.videos.details.related

import android.app.Dialog
import android.content.pm.ActivityInfo
import android.content.res.Resources
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.core.view.isVisible
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.ke.dawaati.R
import com.ke.dawaati.databinding.RelatedVideosDialogFragmentBinding
import com.ke.dawaati.db.model.PracticalsEntity
import com.ke.dawaati.db.model.SubTopicsEntity
import com.ke.dawaati.preference.ConfigurationPrefs
import com.ke.dawaati.uiux.validated.content.videos.ContentPracticalVideosAdapter
import com.ke.dawaati.uiux.validated.content.videos.DisplayVideoSubTopics
import com.ke.dawaati.util.StringConstants
import com.ke.dawaati.util.viewBinding
import org.koin.android.ext.android.inject

class RelatedVideosDialogFragment(
    private val currentVideo: SubTopicsEntity? = null,
    private val displayRelatedVideos: List<DisplayVideoSubTopics> = emptyList(),
    private val currentPracticalVideo: PracticalsEntity? = null,
    private val displayPracticalVideos: List<PracticalsEntity> = emptyList(),
    val subscriptionStatus: Boolean,
    val videoCallBack: () -> (Unit),
    val practicalCallBack: () -> (Unit)
) : BottomSheetDialogFragment() {

    private val binding by viewBinding(RelatedVideosDialogFragmentBinding::bind)

    private val prefs: ConfigurationPrefs by inject()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setCanceledOnTouchOutside(false)
        dialog.setOnShowListener {
            val bottomSheet = dialog.findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)
            val params = bottomSheet.layoutParams
            val height: Int = Resources.getSystem().displayMetrics.heightPixels
            val maxHeight = (height * 0.90).toInt()
            params.height = maxHeight
            bottomSheet.layoutParams = params
            BottomSheetBehavior.from(bottomSheet).apply {
                state = BottomSheetBehavior.STATE_EXPANDED
                skipCollapsed = true
            }
        }
        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = inflater.inflate(R.layout.related_videos_dialog_fragment, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

        setupToolbar()
        setupListAdapter()

        currentVideo?.let {
            renderRelatedVideos(
                relatedVideos = displayRelatedVideos,
                subject = prefs.getSubject().second ?: "",
                level = prefs.getLevel().second ?: ""
            )
        }

        currentPracticalVideo?.let {
            relatedPracticals(
                displayVideos = displayPracticalVideos,
                subject = prefs.getSubject().second ?: "",
                level = prefs.getLevel().second ?: "",
                subscriptionStatus = subscriptionStatus
            )
        }

        binding.videosEmptyState.apply {
            isVisible = displayPracticalVideos.isEmpty() && displayRelatedVideos.isEmpty()
            setEmptyTitle(title = "No related videos")
            setEmptySubTitle(subTitle = "There are no related videos for ${prefs.getSubject().second ?: ""}, ${prefs.getLevel().second ?: ""}")
        }
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener { dismiss() }
    }

    private fun setupListAdapter() {
        binding.apply {
            val customAdapter = if (prefs.getVideoDirection() == StringConstants.SECTIONED)
                contentVideosRelatedAdapter
            else
                contentPracticalVideosAdapter
            relatedVideosList.adapter = customAdapter
        }
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

    private val contentVideosRelatedAdapter by lazy {
        ContentVideosRelatedAdapter(
            selectedVideo = { displaySubTopics ->
                prefs.setVideos(videoID = displaySubTopics.file_id)
                videoCallBack()
                dismiss()
            },
            selectedPremiumVideo = {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        )
    }

    private val contentPracticalVideosAdapter by lazy {
        ContentPracticalVideosAdapter(
            selectedVideo = { displaySubTopics ->
                prefs.setVideos(videoID = displaySubTopics.file_id)
                practicalCallBack()
                dismiss()
            },
            selectedPremiumVideo = {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        )
    }
}
