package com.ke.dawaati.widgets.reader.service

import android.app.IntentService
import android.content.Context
import android.content.Intent
import com.ke.dawaati.widgets.reader.util.FileUtil
import java.io.IOException

class CopyAssetService : IntentService("CopyAssetService") {
    override fun onHandleIntent(intent: Intent?) {
        if (intent != null) {
            val action = intent.action
            if (ACTION_COPY_ASSET == action) {
                val param1 = intent.getStringExtra(EXTRA_ASSET)
                val param2 = intent.getStringExtra(EXTRA_DESTINATION)
                handleActionCopyAsset(param1, param2)
            }
        }
    }

    private fun handleActionCopyAsset(asset: String?, destinationPath: String?) {
        try {
            FileUtil.copyAsset(this, asset, destinationPath)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    companion object {
        private val ACTION_COPY_ASSET: String = com.ke.dawaati.BuildConfig.APPLICATION_ID.toString() + ".copy_asset"
        private val EXTRA_ASSET: String = com.ke.dawaati.BuildConfig.APPLICATION_ID.toString() + ".asset"
        private val EXTRA_DESTINATION: String = com.ke.dawaati.BuildConfig.APPLICATION_ID.toString() + ".destination_path"
        fun startCopyAction(context: Context, asset: String?, destinationPath: String?) {
            val intent = Intent(context, CopyAssetService::class.java)
            intent.action = ACTION_COPY_ASSET
            intent.putExtra(EXTRA_ASSET, asset)
            intent.putExtra(EXTRA_DESTINATION, destinationPath)
            context.startService(intent)
        }
    }
}
