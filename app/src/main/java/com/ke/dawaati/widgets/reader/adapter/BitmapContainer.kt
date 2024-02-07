package com.ke.dawaati.widgets.reader.adapter

import android.graphics.Bitmap

interface BitmapContainer {
    operator fun get(position: Int): Bitmap?
    fun remove(position: Int)
    fun clear()
}
