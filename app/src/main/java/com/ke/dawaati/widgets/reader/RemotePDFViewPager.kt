package com.ke.dawaati.widgets.reader

import android.content.Context
import android.content.res.TypedArray
import android.os.Handler
import android.util.AttributeSet
import androidx.viewpager.widget.ViewPager
import com.ke.dawaati.R
import com.ke.dawaati.widgets.reader.remote.DownloadFile
import com.ke.dawaati.widgets.reader.remote.DownloadFileUrlConnectionImpl
import com.ke.dawaati.widgets.reader.util.FileUtil
import java.io.File

class RemotePDFViewPager @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : ViewPager(context, attrs), DownloadFile.Listener {

    var downloadFile: DownloadFile? = null
    var listener: DownloadFile.Listener? = null

    init {
        init(attrs = attrs)
    }

    fun RemotePDFViewPager(context: Context?, attrs: AttributeSet?) {
        init(attrs)
    }

    private fun init(attrs: AttributeSet?) {
        attrs?.let {
            val a: TypedArray = context.obtainStyledAttributes(attrs, R.styleable.PDFViewPager)
            val pdfPagerUrl = a.getString(R.styleable.PDFViewPager_pdfUrl)
            if (pdfPagerUrl != null && pdfPagerUrl.isNotEmpty()) {
                initDownload(DownloadFileUrlConnectionImpl(context, Handler(), this), pdfPagerUrl)
            }
            a.recycle()
        }
    }

    fun initRemote(context: Context?, pdfUrl: String?, listener: DownloadFile.Listener?) {
        this.listener = listener
        initDownload(
            downloadFile = DownloadFileUrlConnectionImpl(
                context = context!!,
                uiThread = Handler(),
                listener = this
            ),
            pdfUrl = pdfUrl!!
        )
    }

    private fun initDownload(downloadFile: DownloadFile, pdfUrl: String) {
        setDownloader(downloadFile)
        downloadFile.download(
            pdfUrl,
            File(context.cacheDir, FileUtil.extractFileNameFromURL(pdfUrl)).absolutePath
        )
    }

    private fun setDownloader(downloadFile: DownloadFile?) {
        this.downloadFile = downloadFile
    }

    override fun onSuccess(url: String?, destinationPath: String?) {
        listener!!.onSuccess(url, destinationPath)
    }

    override fun onFailure(e: Exception?) {
        listener!!.onFailure(e)
    }

    override fun onProgressUpdate(progress: Int, total: Int) {
        listener!!.onProgressUpdate(progress, total)
    }

    inner class NullListener : DownloadFile.Listener {
        override fun onSuccess(url: String?, destinationPath: String?) {
            /* Empty */
        }

        override fun onFailure(e: Exception?) {
            /* Empty */
        }

        override fun onProgressUpdate(progress: Int, total: Int) {
            /* Empty */
        }
    }
}
