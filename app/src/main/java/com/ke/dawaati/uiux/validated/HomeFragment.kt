package com.ke.dawaati.uiux.validated

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ke.dawaati.R
import com.ke.dawaati.databinding.HomeFragmentBinding
import com.ke.dawaati.db.model.SubjectsEntity
import com.ke.dawaati.permission.PermissionHandler
import com.ke.dawaati.uiux.validated.content.videos.DisplayVideoSubTopics
import com.ke.dawaati.util.UserNameDrawable
import com.ke.dawaati.util.getInitials
import com.ke.dawaati.util.observeEvent
import com.ke.dawaati.util.viewBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeFragment : Fragment(R.layout.home_fragment) {

    private val viewModel: HomeViewModel by viewModel()

    private val binding by viewBinding(HomeFragmentBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        setupObservers()
        setupListAdapter()
        initViews()
        initComponentsNavHeader()
        setOnClickListeners()

        viewModel.loadSubjects()

        PermissionHandler(activity = requireActivity()).loadPermissions()
    }

    private fun setupToolbar() {
        binding.apply {
            toolbar.setNavigationOnClickListener { findNavController().navigateUp() }
        }
    }

    private fun initViews() {
        binding.apply {
            subscribeGroup.isGone = viewModel.getSubscriptionStatus()
        }
    }

    private fun setOnClickListeners() {
        binding.apply {
            classLevelClick.setOnClickListener {
                viewModel.loadFormSelect()
            }

            rating.setOnClickListener {
                viewModel.rateApp(requireActivity())
            }

            ratingShare.setOnClickListener {
                viewModel.shareApp(requireActivity())
            }

            subscribeBanner.setOnClickListener {
                viewModel.openSubscribe()
            }

            subscribeBannerAction.setOnClickListener {
                viewModel.openSubscribe()
            }
            // TODO: Revisit this later
//            requireParentFragment().view?.apply {
//                isFocusableInTouchMode = true
//                requestFocus()
//                setOnKeyListener { _, keyCode, _ ->
//                    if (keyCode == KeyEvent.KEYCODE_BACK) {
//                        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
//                            drawerLayout.closeDrawer(GravityCompat.START)
//                        } else {
//                            lifecycleScope.launchWhenResumed {
//                                findNavController().navigateUp()
//                            }
//                        }
//                        true
//                    } else {
//                        false
//                    }
//                }
//            }
        }
    }

    private fun initComponentsNavHeader() {
        binding.apply {
            val accountDetails = viewModel.loadAccountDetails()

            profileName.text = accountDetails?.username ?: accountDetails?.name ?: ""
            emailValue.text = if (viewModel.getSubscriptionStatus()) "Premium account" else "Basic account"
            profileAvatar.apply {
                setImageDrawable(
                    UserNameDrawable(
                        initials = profileName.text.toString().getInitials(),
                        context = requireContext()
                    )
                )
            }

            //  viewModel.openCareers()
            //  viewModel.logout()
        }
    }

    private fun setupObservers() {
        viewModel.uiState.observe(viewLifecycleOwner) {
            when (it) {
                HomeUIState.Loading -> {}
                is HomeUIState.Subjects -> renderSubjects(
                    displayVideos = it.displayVideos,
                    subjectsEntity = it.subjects
                )
                is HomeUIState.Error -> {}
                is HomeUIState.SelectedLevel -> binding.classLevel.text = it.level
            }
        }

        viewModel.action.observeEvent(viewLifecycleOwner) {
            when (it) {
                is HomeActions.Navigate -> findNavController().navigate(it.destination)
                is HomeActions.BottomNavigate ->
                    it.bottomSheet.show(parentFragmentManager, it.bottomSheet.tag)
            }
        }
    }

    private fun setupListAdapter() {
        binding.apply {
//            val snapHelper = PagerSnapHelper()
//            snapHelper.attachToRecyclerView(subjectsList)

            // Grid layout, 2 columns
            // subjectsList.layoutManager = GridLayoutManager(requireContext(), 2, RecyclerView.HORIZONTAL, false)
            subjectsList.adapter = homeSubjectsAdapter

            continueWatchingList.apply {
                layoutManager = GridLayoutManager(requireContext(), 1, RecyclerView.HORIZONTAL, false)
                adapter = homeVideosAdapter
            }
        }
    }

    private fun renderSubjects(
        displayVideos: List<DisplayVideoSubTopics>,
        subjectsEntity: List<SubjectsEntity>
    ) {
        homeSubjectsAdapter.submitList(subjectsEntity)
        homeVideosAdapter.submitList(displayVideos)

        binding.apply {
            continueWatchingList.isVisible = displayVideos.isNotEmpty()
            continueWatchingGroup.isGone = displayVideos.isEmpty()
        }
    }

    private val homeSubjectsAdapter: HomeSubjectsAdapter by lazy {
        HomeSubjectsAdapter(
            subjectSelected = { subjectsEntity ->
                when (subjectsEntity.subjectID) {
                    "-1" -> {
                        // Action needed
                    }
                    "-2" -> {
                        val url = "https://api.whatsapp.com/send?phone=+${745001456}"
                        val i = Intent(Intent.ACTION_VIEW)
                        i.data = Uri.parse(url)
                        startActivity(i)
                    }
                    else -> {
                        viewModel.viewSubjectContent(subjectsEntity = subjectsEntity)
                    }
                }
            }
        )
    }

    private val homeVideosAdapter: HomeVideosAdapter by lazy {
        HomeVideosAdapter(
            videoSelectedSelected = { videoEntity ->
                viewModel.viewVideoContent(video = videoEntity, context = requireContext())
            }
        )
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadAnalytics()
        viewModel.syncAnalytics()
    }
}
