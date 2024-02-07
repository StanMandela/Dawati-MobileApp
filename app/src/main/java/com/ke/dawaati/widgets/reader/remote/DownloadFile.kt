package com.ke.dawaati.widgets.reader.remote

interface DownloadFile {
    fun download(url: String?, destinationPath: String?)
    interface Listener {
        fun onSuccess(url: String?, destinationPath: String?)
        fun onFailure(e: Exception?)
        fun onProgressUpdate(progress: Int, total: Int)
    }
}
