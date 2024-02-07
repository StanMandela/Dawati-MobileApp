package com.ke.dawaati.widgets.reader.util

import android.content.Context
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream

object FileUtil {
    @Throws(IOException::class)
    fun copyAsset(ctx: Context, assetName: String?, destinationPath: String?): Boolean {
        val `in` = ctx.assets.open(assetName!!)
        val f = File(destinationPath)
        f.createNewFile()
        val out: OutputStream = FileOutputStream(File(destinationPath))
        val buffer = ByteArray(1024)
        var read: Int
        while (`in`.read(buffer).also { read = it } != -1) {
            out.write(buffer, 0, read)
        }
        `in`.close()
        out.close()
        return true
    }

    fun extractFileNameFromURL(url: String): String {
        return url.substring(url.lastIndexOf('/') + 1)
    }
}
