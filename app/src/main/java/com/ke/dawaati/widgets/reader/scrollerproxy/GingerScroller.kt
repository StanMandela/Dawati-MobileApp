package com.ke.dawaati.widgets.reader.scrollerproxy

import android.annotation.TargetApi
import android.content.Context
import android.widget.OverScroller

@TargetApi(9)
open class GingerScroller(context: Context?) : ScrollerProxy() {
    protected val mScroller: OverScroller = OverScroller(context)
    private var mFirstScroll = false
    override fun computeScrollOffset(): Boolean {
        // Workaround for first scroll returning 0 for the direction of the edge it hits.
        // Simply recompute values.
        if (mFirstScroll) {
            mScroller.computeScrollOffset()
            mFirstScroll = false
        }
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
        mScroller.fling(startX, startY, velocityX, velocityY, minX, maxX, minY, maxY, overX, overY)
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
