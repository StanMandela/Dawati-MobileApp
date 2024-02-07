package com.ke.dawaati.widgets.reader.asset

interface CopyAsset {
    fun copy(assetName: String, destinationPath: String)
    interface Listener {
        fun success(assetName: String?, destinationPath: String?)
        fun failure(e: Exception?)
    }
}
