package com.ke.dawaati.widgets.reader.gestures

import android.annotation.TargetApi
import android.content.Context
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.ScaleGestureDetector.OnScaleGestureListener

@TargetApi(8)
class FroyoGestureDetector(context: Context?) : EclairGestureDetector(context) {
    protected val mDetector: ScaleGestureDetector
    override val isScaling: Boolean
        get() = mDetector.isInProgress

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        mDetector.onTouchEvent(ev)
        return super.onTouchEvent(ev)
    }

    init {
        val mScaleListener: OnScaleGestureListener = object : OnScaleGestureListener {
            override fun onScale(detector: ScaleGestureDetector): Boolean {
                val scaleFactor = detector.scaleFactor
                if (java.lang.Float.isNaN(scaleFactor) || java.lang.Float.isInfinite(scaleFactor)) return false
                mListener!!.onScale(
                    scaleFactor,
                    detector.focusX, detector.focusY
                )
                return true
            }

            override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
                return true
            }

            override fun onScaleEnd(detector: ScaleGestureDetector) {
                // NO-OP
            }
        }
        mDetector = ScaleGestureDetector(context!!, mScaleListener)
    }
}
