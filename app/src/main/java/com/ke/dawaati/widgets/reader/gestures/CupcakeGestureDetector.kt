package com.ke.dawaati.widgets.reader.gestures

import android.content.Context
import android.util.Log
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.ViewConfiguration

open class CupcakeGestureDetector(context: Context?) : GestureDetector {
    protected var mListener: OnGestureListener? = null
    var mLastTouchX = 0f
    var mLastTouchY = 0f
    val mTouchSlop: Float
    val mMinimumVelocity: Float
    override fun setOnGestureListener(listener: OnGestureListener?) {
        mListener = listener
    }

    private var mVelocityTracker: VelocityTracker? = null
    private var mIsDragging = false
    open fun getActiveX(ev: MotionEvent): Float {
        return ev.x
    }

    open fun getActiveY(ev: MotionEvent): Float {
        return ev.y
    }

    override val isScaling: Boolean
        get() = false

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                mVelocityTracker = VelocityTracker.obtain()
                if (null != mVelocityTracker) {
                    mVelocityTracker!!.addMovement(ev)
                } else {
                    Log.i(LOG_TAG, "Velocity tracker is null")
                }
                mLastTouchX = getActiveX(ev)
                mLastTouchY = getActiveY(ev)
                mIsDragging = false
            }
            MotionEvent.ACTION_MOVE -> {
                val x = getActiveX(ev)
                val y = getActiveY(ev)
                val dx = x - mLastTouchX
                val dy = y - mLastTouchY
                if (!mIsDragging) {
                    // Use Pythagoras to see if drag length is larger than
                    // touch slop
                    mIsDragging = Math.sqrt(dx * dx + (dy * dy).toDouble()).toFloat() >= mTouchSlop
                }
                if (mIsDragging) {
                    mListener!!.onDrag(dx, dy)
                    mLastTouchX = x
                    mLastTouchY = y
                    if (null != mVelocityTracker) {
                        mVelocityTracker!!.addMovement(ev)
                    }
                }
            }
            MotionEvent.ACTION_CANCEL -> {

                // Recycle Velocity Tracker
                if (null != mVelocityTracker) {
                    mVelocityTracker!!.recycle()
                    mVelocityTracker = null
                }
            }
            MotionEvent.ACTION_UP -> {
                if (mIsDragging) {
                    if (null != mVelocityTracker) {
                        mLastTouchX = getActiveX(ev)
                        mLastTouchY = getActiveY(ev)

                        // Compute velocity within the last 1000ms
                        mVelocityTracker!!.addMovement(ev)
                        mVelocityTracker!!.computeCurrentVelocity(1000)
                        val vX = mVelocityTracker!!.xVelocity
                        val vY = mVelocityTracker!!
                            .yVelocity

                        // If the velocity is greater than minVelocity, call
                        // listener
                        if (Math.max(Math.abs(vX), Math.abs(vY)) >= mMinimumVelocity) {
                            mListener!!.onFling(
                                mLastTouchX, mLastTouchY, -vX,
                                -vY
                            )
                        }
                    }
                }

                // Recycle Velocity Tracker
                if (null != mVelocityTracker) {
                    mVelocityTracker!!.recycle()
                    mVelocityTracker = null
                }
            }
        }
        return true
    }

    companion object {
        private const val LOG_TAG = "CupcakeGestureDetector"
    }

    init {
        val configuration = ViewConfiguration
            .get(context!!)
        mMinimumVelocity = configuration.scaledMinimumFlingVelocity.toFloat()
        mTouchSlop = configuration.scaledTouchSlop.toFloat()
    }
}
