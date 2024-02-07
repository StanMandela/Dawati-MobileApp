package com.ke.dawaati.widgets.reader.gestures

import android.annotation.TargetApi
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.view.MotionEvent
import android.view.View

object Compat {
    private const val SIXTY_FPS_INTERVAL = 1000 / 60
    fun postOnAnimation(view: View, runnable: Runnable) {
        if (VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN) {
            postOnAnimationJellyBean(view, runnable)
        } else {
            view.postDelayed(runnable, SIXTY_FPS_INTERVAL.toLong())
        }
    }

    @TargetApi(16)
    private fun postOnAnimationJellyBean(view: View, runnable: Runnable) {
        view.postOnAnimation(runnable)
    }

    fun getPointerIndex(action: Int): Int {
        return if (VERSION.SDK_INT >= VERSION_CODES.HONEYCOMB) getPointerIndexHoneyComb(action) else getPointerIndexEclair(action)
    }

    @TargetApi(VERSION_CODES.ECLAIR)
    private fun getPointerIndexEclair(action: Int): Int {
        return action and MotionEvent.ACTION_POINTER_ID_MASK shr MotionEvent.ACTION_POINTER_ID_SHIFT
    }

    @TargetApi(VERSION_CODES.HONEYCOMB)
    private fun getPointerIndexHoneyComb(action: Int): Int {
        return action and MotionEvent.ACTION_POINTER_INDEX_MASK shr MotionEvent.ACTION_POINTER_INDEX_SHIFT
    }
}
