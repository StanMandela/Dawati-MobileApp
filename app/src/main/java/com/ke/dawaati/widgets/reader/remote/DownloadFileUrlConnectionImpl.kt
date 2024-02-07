package com.ke.dawaati.widgets.reader.remote

import android.content.Context
import android.os.Handler
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL

class DownloadFileUrlConnectionImpl(var context: Context, var uiThread: Handler?, listener: DownloadFile.Listener?) :
    DownloadFile {
    var listener: DownloadFile.Listener? = NullListener()
    override fun download(url: String?, destinationPath: String?) {
        Thread {
            try {
                val file = File(destinationPath)
                val fileOutput = FileOutputStream(file)
                var urlConnection: HttpURLConnection? = null
                val urlObj = URL(url)
                urlConnection = urlObj.openConnection() as HttpURLConnection
                val totalSize = urlConnection!!.contentLength
                var downloadedSize = 0
                var counter = 0
                val buffer = ByteArray(BUFFER_LEN)
                var bufferLength = 0
                val `in`: InputStream = BufferedInputStream(urlConnection.inputStream)
                while (`in`.read(buffer).also { bufferLength = it } > 0) {
                    fileOutput.write(buffer, 0, bufferLength)
                    downloadedSize += bufferLength
                    counter += bufferLength
                    if (listener != null && counter > NOTIFY_PERIOD) {
                        notifyProgressOnUiThread(downloadedSize, totalSize)
                        counter = 0
                    }
                }
                urlConnection.disconnect()
                fileOutput.close()
            } catch (e: MalformedURLException) {
                notifyFailureOnUiThread(e)
            } catch (e: IOException) {
                notifyFailureOnUiThread(e)
            }
            notifySuccessOnUiThread(url, destinationPath)
        }.start()
    }

    protected fun notifySuccessOnUiThread(url: String?, destinationPath: String?) {
        if (uiThread == null) {
            return
        }
        uiThread!!.post { listener!!.onSuccess(url, destinationPath) }
    }

    protected fun notifyFailureOnUiThread(e: Exception?) {
        if (uiThread == null) {
            return
        }
        uiThread!!.post { listener!!.onFailure(e) }
    }

    private fun notifyProgressOnUiThread(downloadedSize: Int, totalSize: Int) {
        if (uiThread == null) {
            return
        }
        uiThread!!.post { listener!!.onProgressUpdate(downloadedSize, totalSize) }
    }

    protected inner class NullListener : DownloadFile.Listener {
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

    companion object {
        private const val KILOBYTE = 1024
        private const val BUFFER_LEN = 1 * KILOBYTE
        private const val NOTIFY_PERIOD = 150 * KILOBYTE
    }

    init {
        this.listener = listener
    }
}
