package com.ke.dawaati.uiux.validated.profile

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.ke.dawaati.R
import com.ke.dawaati.databinding.FragmentProfileBinding
import com.ke.dawaati.db.repo.UserDetailsRepository
import com.ke.dawaati.util.formatToDisplayDate
import com.ke.dawaati.util.viewBinding
import org.koin.android.ext.android.inject

class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private val binding by viewBinding(FragmentProfileBinding::bind)
    private val userDetailsRepository: UserDetailsRepository by inject()

    private var bottomSheet: BottomSheetDialogFragment? = null

    private var isResume = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupClickListeners()
        renderProfileDetails()
    }

    private fun setupClickListeners() {
        binding.apply {
            logoutButton.root.setOnClickListener {
                findNavController().navigate(ProfileFragmentDirections.logout())
            }
        }
    }

    private fun renderProfileDetails() {
        val userDetails = userDetailsRepository.loadUserDetails()
        val subscriptionDetail = userDetailsRepository.loadSubscriptionDetails()
        binding.apply {
            userDetails?.let { user ->
                nameValue.text = user.username
                emailValue.text = user.email
                mobileNumberValue.text = user.mobile

                schoolValue.text = user.name
                schoolLevelValue.text = user.level_name
            }

            subscriptionTypeValue.text = subscriptionDetail.subscriptionType
            subscriptionStatusValue.text = subscriptionDetail.status
            subscriptionDurationValue.text = "${subscriptionDetail.startDate.formatToDisplayDate()} " +
                "to ${subscriptionDetail.expiry.formatToDisplayDate()}"
        }
    }

    override fun onResume() {
        super.onResume()
        if (isResume)
            renderProfileDetails()
        isResume = true
    }

    override fun onDestroy() {
        super.onDestroy()
        bottomSheet?.dismiss()
    }
}
