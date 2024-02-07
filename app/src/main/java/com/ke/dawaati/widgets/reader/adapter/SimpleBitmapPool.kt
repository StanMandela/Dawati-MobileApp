package com.ke.dawaati.widgets.reader.adapter

import android.graphics.Bitmap
import android.graphics.Color

class SimpleBitmapPool(params: PdfRendererParams) : BitmapContainer {
    var bitmaps: Array<Bitmap?>
    private val poolSize: Int
    private val width: Int
    private val height: Int
    private val config: Bitmap.Config?
    private fun getPoolSize(offScreenSize: Int): Int {
        return offScreenSize * 2 + 1
    }

    fun getBitmap(position: Int): Bitmap? {
        val index = getIndexFromPosition(position)
        if (bitmaps[index] == null) {
            createBitmapAtIndex(index)
        }
        bitmaps[index]!!.eraseColor(Color.TRANSPARENT)
        return bitmaps[index]
    }

    override fun get(position: Int): Bitmap? {
        return getBitmap(position)
    }

    override fun remove(position: Int) {
        bitmaps[position]!!.recycle()
        bitmaps[position] = null
    }

    override fun clear() {
        recycleAll()
    }

    protected fun recycleAll() {
        for (i in 0 until poolSize) {
            if (bitmaps[i] != null) {
                bitmaps[i]!!.recycle()
                bitmaps[i] = null
            }
        }
    }

    protected fun createBitmapAtIndex(index: Int) {
        val bitmap = Bitmap.createBitmap(width, height, config!!)
        bitmaps[index] = bitmap
    }

    protected fun getIndexFromPosition(position: Int): Int {
        return position % poolSize
    }

    init {
        poolSize = getPoolSize(params.offScreenSize)
        width = params.width
        height = params.height
        config = params.config
        bitmaps = arrayOfNulls(poolSize)
    }
}
