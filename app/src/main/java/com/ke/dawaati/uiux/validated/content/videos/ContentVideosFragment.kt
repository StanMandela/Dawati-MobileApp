package com.ke.dawaati.uiux.validated.content.videos

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayoutMediator
import com.ke.dawaati.R
import com.ke.dawaati.databinding.SubjectContentVideosFragmentBinding
import com.ke.dawaati.preference.ConfigurationPrefs
import com.ke.dawaati.uiux.validated.content.ebooks.TabPagerAdapter
import com.ke.dawaati.uiux.validated.content.videos.tabs.ContentVideoClassFragment
import com.ke.dawaati.uiux.validated.content.videos.tabs.ContentVideoExamsFragment
import com.ke.dawaati.uiux.validated.content.videos.tabs.ContentVideoLabsFragment
import com.ke.dawaati.util.viewBinding
import org.koin.android.ext.android.inject

class ContentVideosFragment : Fragment(R.layout.subject_content_videos_fragment) {

    private val binding by viewBinding(SubjectContentVideosFragmentBinding::bind)

    private val prefs: ConfigurationPrefs by inject()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        setupToolbar()
        setupVideosNavigation()
    }

    private fun setupToolbar() {
        binding.apply {
            title.text = "Videos, ${prefs.getLevel().second}"
        }
    }

    private fun setupVideosNavigation() {
        binding.apply {
            val homeAdapter = TabPagerAdapter(fragment = this@ContentVideosFragment)

            tabPager.adapter = homeAdapter

            val nonLabsSubject = listOf(
                "Mathematics",
                "English",
                "Kiswahili",
                "History"
            )

            val fragmentsNames = mutableListOf<String>()
            fragmentsNames.add("Class")
            if (!nonLabsSubject.contains(prefs.getSubject().second))
                fragmentsNames.add("Labs")
            fragmentsNames.add("Exams")

            val fragmentsList = mutableListOf<Fragment>()
            fragmentsList.add(ContentVideoClassFragment())
            if (!nonLabsSubject.contains(prefs.getSubject().second))
                fragmentsList.add(ContentVideoLabsFragment())
            fragmentsList.add(ContentVideoExamsFragment())

            homeAdapter.addFragments(
                listOfFragments = fragmentsList
            )

            TabLayoutMediator(tabLayout, tabPager) { tab, position ->
                tab.text = fragmentsNames[position]
            }.attach()
        }
    }
}
