package com.ke.dawaati.widgets.reader.scrollerproxy

import android.content.Context
import android.widget.Scroller

class PreGingerScroller(context: Context?) : ScrollerProxy() {
    private val mScroller: Scroller = Scroller(context)
    override fun computeScrollOffset(): Boolean {
        return mScroller.computeScrollOffset()
    }

    override fun fling(
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
    ) {
        mScroller.fling(startX, startY, velocityX, velocityY, minX, maxX, minY, maxY)
    }

    override fun forceFinished(finished: Boolean) {
        mScroller.forceFinished(finished)
    }

    override val isFinished: Boolean
        get() = mScroller.isFinished
    override val currX: Int
        get() = mScroller.currX
    override val currY: Int
        get() = mScroller.currY
}
