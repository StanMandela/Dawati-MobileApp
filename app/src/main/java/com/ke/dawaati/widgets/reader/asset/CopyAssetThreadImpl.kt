package com.ke.dawaati.widgets.reader.asset

import android.content.Context
import android.os.Handler
import com.ke.dawaati.widgets.reader.util.FileUtil
import java.io.IOException

class CopyAssetThreadImpl : CopyAsset {
    var context: Context
    var uiThread: Handler?
    var listener: CopyAsset.Listener = NullListener()

    constructor(context: Context, uiThread: Handler?, listener: CopyAsset.Listener?) {
        this.context = context
        this.uiThread = uiThread
        if (listener != null) {
            this.listener = listener
        }
    }

    constructor(context: Context, uiThread: Handler?) {
        this.context = context
        this.uiThread = uiThread
    }

    override fun copy(assetName: String, destinationPath: String) {
        Thread {
            try {
                FileUtil.copyAsset(context, assetName, destinationPath)
                notifySuccess(assetName, destinationPath)
            } catch (e: IOException) {
                notifyError(e)
            }
        }.start()
    }

    private fun notifySuccess(assetName: String, destinationPath: String) {
        if (uiThread == null) {
            return
        }
        uiThread!!.post { listener.success(assetName, destinationPath) }
    }

    private fun notifyError(e: IOException) {
        if (uiThread == null) {
            return
        }
        uiThread!!.post { listener.failure(e) }
    }

    protected inner class NullListener : CopyAsset.Listener {
        override fun success(assetName: String?, destinationPath: String?) {}
        override fun failure(e: Exception?) {}
    }
}
