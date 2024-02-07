package com.ke.dawaati.uiux.validated.content.ebooks

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.ke.dawaati.R
import com.ke.dawaati.databinding.SubjectContentEbooksFragmentBinding
import com.ke.dawaati.preference.ConfigurationPrefs
import com.ke.dawaati.uiux.validated.content.ebooks.tabs.ContentEBooksClassFragment
import com.ke.dawaati.uiux.validated.content.ebooks.tabs.ContentEBooksRevisionFragment
import com.ke.dawaati.util.viewBinding
import org.koin.android.ext.android.inject

class ContentEBooksFragment : Fragment(R.layout.subject_content_ebooks_fragment) {

    private val binding by viewBinding(SubjectContentEbooksFragmentBinding::bind)

    private val prefs: ConfigurationPrefs by inject()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        setupEBookNavigation()
    }

    private fun setupToolbar() {
        binding.apply {
            title.text = "E-books, ${prefs.getLevel().second}"
        }
    }

    private fun setupEBookNavigation() {
        binding.apply {
            val homeAdapter = TabPagerAdapter(fragment = this@ContentEBooksFragment)

            tabPager.adapter = homeAdapter

            val fragmentsNames = listOf("Class", "Revision")

            val fragmentsList = listOf(
                ContentEBooksClassFragment(),
                ContentEBooksRevisionFragment()
            )

            homeAdapter.addFragments(
                listOfFragments = fragmentsList
            )

            TabLayoutMediator(tabLayout, tabPager) { tab, position ->
                tab.text = fragmentsNames[position]
            }.attach()
        }
    }
}

class TabPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    private val fragmentsList = mutableListOf<Fragment>()

    override fun getItemCount(): Int = fragmentsList.size

    override fun createFragment(position: Int): Fragment {
        return fragmentsList[position]
    }

    fun addFragments(listOfFragments: List<Fragment>) {
        fragmentsList.clear()
        fragmentsList.addAll(listOfFragments)
    }
}
