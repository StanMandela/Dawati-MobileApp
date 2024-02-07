package com.ke.dawaati.uiux.validated.content.ebooks.details

import android.annotation.SuppressLint
import android.net.http.SslError
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.webkit.SslErrorHandler
import android.webkit.WebView
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.ke.dawaati.R
import com.ke.dawaati.databinding.SubjectContentEbooksDetailsFragmentBinding
import com.ke.dawaati.db.model.EBooksEntity
import com.ke.dawaati.preference.ConfigurationPrefs
import com.ke.dawaati.util.EncryptUtil
import com.ke.dawaati.util.viewBinding
import com.ke.dawaati.widgets.reader.adapter.PDFPagerAdapter
import com.ke.dawaati.widgets.reader.remote.DownloadFile
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File

class ContentEBookDetailsFragment : Fragment(R.layout.subject_content_ebooks_details_fragment), DownloadFile.Listener {

    private val viewModel: ContentEBookDetailsViewModel by viewModel()

    private val binding by viewBinding(SubjectContentEbooksDetailsFragmentBinding::bind)

    private val prefs: ConfigurationPrefs by inject()

    private val encrypt = EncryptUtil("Kelvinmkioko@gmail.com")

    private var checkOnPageStartedCalled = false
    private var fileName: String = ""
    private var livePDFURL: String = ""
    private var decrypted: File? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        setupObservers()

        viewModel.loadAllEBookContent(context = requireContext())
    }

    private fun setupToolbar() {
        binding.apply {
            toolbar.setNavigationOnClickListener { findNavController().navigateUp() }
            toolbarTitle.text = "${prefs.getSubject().second} ${prefs.getLevel().second}"
        }
    }

    private fun setupObservers() {
        viewModel.uiState.observe(viewLifecycleOwner) {
            when (it) {
                is ContentEBookDetailsUIState.EBooksContent -> {
                    renderEBookDetails(currentEBook = it.currentEBooks)
                }
            }
        }
    }

    private fun renderEBookDetails(currentEBook: EBooksEntity) {
        binding.apply {
            val listener: DownloadFile.Listener = this@ContentEBookDetailsFragment

            fileName = currentEBook.file_name

            val encrypted = if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                File(
                    Environment.getExternalStorageDirectory().toString() +
                        File.separator + "Dawati" +
                        File.separator + prefs.getSubject().second +
                        File.separator + prefs.getLevel().second +
                        File.separator + "eBooks" +
                        File.separator + currentEBook.file_name + "-enc"
                )
            } else {
                File(
                    requireContext().externalCacheDir.toString(),
                    File.separator + "Dawati" +
                        File.separator + prefs.getSubject().second +
                        File.separator + prefs.getLevel().second +
                        File.separator + "eBooks" +
                        File.separator + currentEBook.file_name + "-enc"
                )
            }

            if (encrypted.exists()) {
                decrypted = if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                    File(
                        Environment.getExternalStorageDirectory().toString() +
                            File.separator + "Dawati" +
                            File.separator + prefs.getSubject().second +
                            File.separator + prefs.getLevel().second +
                            File.separator + "eBooks" +
                            File.separator + currentEBook.file_name + ".pdf"
                    )
                } else {
                    File(
                        requireContext().externalCacheDir.toString(),
                        File.separator + "Dawati" +
                            File.separator + prefs.getSubject().second +
                            File.separator + prefs.getLevel().second +
                            File.separator + "eBooks" +
                            File.separator + currentEBook.file_name + ".pdf"
                    )
                }
                encrypt.decryptFile(encrypted, decrypted, "Kelvinmkioko@gmail.com")

                pdfReader.isVisible = true
                pdfReaderWebView.isGone = true
                pdfReader.initRemote(requireContext(), decrypted.toString(), listener)
            } else {
                eBooksLoadingLargeState.setLoadingTitle(title = "Loading  $fileName")
                pdfReader.isGone = true
                pdfReaderWebView.isVisible = true
                livePDFURL = (currentEBook.file_url + currentEBook.file_name + ".pdf").replace(
                    " ", "%20"
                )
                loadTermsAndCondition(path = livePDFURL)
            }
        }
    }

    override fun onSuccess(url: String?, destinationPath: String?) {
        if (view != null) {
            binding.apply {
                eBooksLoadingLargeState.isGone = true
                eBooksEmptyState.isGone = true
                pdfReader.apply {
                    adapter = PDFPagerAdapter(requireContext(), url!!)
                    isVisible = true
                }
            }
        }
    }

    override fun onFailure(e: Exception?) {
        if (view != null) {
            binding.apply {
                eBooksLoadingLargeState.isGone = true
                eBooksEmptyState.isVisible = true
                pdfReader.isGone = true
            }
        }
    }

    override fun onProgressUpdate(progress: Int, total: Int) {
        if (view != null) {
            binding.apply {
                eBooksLoadingLargeState.isVisible = true
                eBooksEmptyState.isGone = true
                pdfReader.isGone = true
            }
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun loadTermsAndCondition(path: String) {
        binding.apply {
            checkOnPageStartedCalled = false
            pdfReaderWebView.invalidate()
            pdfReaderWebView.settings.loadWithOverviewMode = true
            pdfReaderWebView.webViewClient = WebViewClient()
            pdfReaderWebView.settings.setSupportZoom(true)
            pdfReaderWebView.settings.javaScriptEnabled = true
            val cleanURL = "https://drive.google.com/viewerng/viewer?embedded=true&url=$path"

            eBooksLoadingLargeState.setLoadingTitle(title = "Loading  $fileName")
            eBooksLoadingLargeState.isVisible = true
            pdfReaderWebView.loadUrl(cleanURL)
        }
    }

    inner class WebViewClient : android.webkit.WebViewClient() {
        override fun shouldOverrideUrlLoading(webView: WebView, url: String): Boolean {
            webView.loadUrl(url)
            return false
        }

        override fun onPageCommitVisible(webView: WebView?, url: String?) {
            super.onPageCommitVisible(webView, url)
            checkOnPageStartedCalled = true
        }

        override fun onPageFinished(webView: WebView, url: String) {
            super.onPageFinished(webView, url)
            if (view != null)
                binding.eBooksLoadingLargeState.isGone = true
            if (view != null && !checkOnPageStartedCalled) {
                loadTermsAndCondition(path = livePDFURL)
            }
        }

        override fun onReceivedSslError(
            view: WebView?,
            handler: SslErrorHandler,
            error: SslError?
        ) {
        }

        override fun onReceivedError(
            view: WebView?,
            errorCod: Int,
            description: String,
            failingUrl: String?
        ) {
            binding.eBooksEmptyState.isGone = true
        }
    }

    override fun onPause() {
        viewModel.closeEbookAnalytics()
        super.onPause()
    }

    override fun onDestroy() {
        viewModel.closeEbookAnalytics()
        super.onDestroy()
        decrypted?.delete()
    }
}
