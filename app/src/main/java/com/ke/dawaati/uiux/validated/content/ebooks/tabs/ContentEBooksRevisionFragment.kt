package com.ke.dawaati.uiux.validated.content.ebooks.tabs

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.ke.dawaati.R
import com.ke.dawaati.databinding.SubjectContentEbooksRevisionFragmentBinding
import com.ke.dawaati.uiux.validated.content.ebooks.ContentEBooksActions
import com.ke.dawaati.uiux.validated.content.ebooks.ContentEBooksAdapter
import com.ke.dawaati.uiux.validated.content.ebooks.ContentEBooksUIState
import com.ke.dawaati.uiux.validated.content.ebooks.ContentEBooksViewModel
import com.ke.dawaati.uiux.validated.content.ebooks.DisplayEBooks
import com.ke.dawaati.util.observeEvent
import com.ke.dawaati.util.viewBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class ContentEBooksRevisionFragment : Fragment(R.layout.subject_content_ebooks_revision_fragment) {

    private val viewModel: ContentEBooksViewModel by viewModel()

    private val binding by viewBinding(SubjectContentEbooksRevisionFragmentBinding::bind)

    private val mainNavController: NavController by lazy { requireActivity().findNavController(R.id.navHostFragment) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupObservers()
        setupListAdapter()

        viewModel.loadRevisionBooks()
    }

    private fun setupObservers() {
        viewModel.uiState.observe(viewLifecycleOwner) {
            when (it) {
                ContentEBooksUIState.Loading -> renderLoader()
                is ContentEBooksUIState.EBooksContent -> {
                    Toast.makeText(requireContext(), "${it.eBooks.isEmpty()}", Toast.LENGTH_SHORT).show()
                    renderEBooks(
                        eBookEntity = it.eBooks,
                        subject = it.subject,
                        level = it.level
                    )
                    if (!it.updateRequest) {
                        renderLoaderBanner()
                    }
                }
                is ContentEBooksUIState.Error -> {}
            }
        }

        viewModel.action.observeEvent(viewLifecycleOwner) {
            when (it) {
                is ContentEBooksActions.Navigate -> mainNavController.navigate(it.destination)
            }
        }
    }

    private fun setupListAdapter() {
        binding.apply {
            revisionList.adapter = contentEBooksAdapter
        }
    }

    private fun renderLoader() {
        binding.apply {
            revisionList.isGone = true
            revisionEmptyState.isGone = true
            revisionLoadingLargeState.isVisible = true
            revisionLoadingBannerState.isGone = true
        }
    }

    private fun renderLoaderBanner() {
        binding.apply {
            revisionList.isVisible = true
            revisionEmptyState.isGone = true
            revisionLoadingLargeState.isGone = true
            revisionLoadingBannerState.isVisible = true
        }
    }

    private fun renderEBooks(
        eBookEntity: List<DisplayEBooks>,
        subject: String,
        level: String
    ) {
        binding.apply {
            revisionList.isVisible = eBookEntity.isNotEmpty()
            revisionEmptyState.isVisible = eBookEntity.isEmpty()
            revisionEmptyState.apply {
                isVisible = eBookEntity.isEmpty()
                setEmptySubTitle(subTitle = "There are no revision e-books for ${viewModel.getSubject()}, ${viewModel.getLevel()}")
            }
            revisionLoadingLargeState.isGone = true
            revisionLoadingBannerState.isGone = true
            contentEBooksAdapter.setEBooks(
                eBookEntity = eBookEntity,
                subject = subject,
                level = level
            )
        }
    }

    private val contentEBooksAdapter: ContentEBooksAdapter by lazy {
        ContentEBooksAdapter(
            selectedEBook = { displayEBooks ->
                viewModel.viewEBooks(displayEBooks = displayEBooks)
            }
        )
    }
}
