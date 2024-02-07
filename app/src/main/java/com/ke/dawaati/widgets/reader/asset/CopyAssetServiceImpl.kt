package com.ke.dawaati.widgets.reader.asset

import android.content.Context
import com.ke.dawaati.widgets.reader.service.CopyAssetService

class CopyAssetServiceImpl(private val context: Context) : CopyAsset {
    override fun copy(assetName: String, destinationPath: String) {
        CopyAssetService.startCopyAction(context, assetName, destinationPath)
    }
}
