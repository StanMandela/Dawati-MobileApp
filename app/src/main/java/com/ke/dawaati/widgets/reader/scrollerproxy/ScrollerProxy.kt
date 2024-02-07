package com.ke.dawaati.widgets.reader.scrollerproxy

import android.content.Context
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES

abstract class ScrollerProxy {
    abstract fun computeScrollOffset(): Boolean
    abstract fun fling(
        startX: Int,
        startY: Int,
        velocityX: Int,
        velocityY: Int,
        minX: Int,
        maxX: Int,
        minY: Int,
        maxY: Int,
        overX: Int,
        overY: Int
    )

    abstract fun forceFinished(finished: Boolean)
    abstract val isFinished: Boolean
    abstract val currX: Int
    abstract val currY: Int

    companion object {
        fun getScroller(context: Context?): ScrollerProxy {
            return when {
                VERSION.SDK_INT < VERSION_CODES.GINGERBREAD -> {
                    PreGingerScroller(context)
                }
                VERSION.SDK_INT < VERSION_CODES.ICE_CREAM_SANDWICH -> {
                    GingerScroller(context)
                }
                else -> {
                    IcsScroller(context)
                }
            }
        }
    }
}
