package com.ke.dawaati.widgets.reader.gestures

import android.view.MotionEvent

interface GestureDetector {
    fun onTouchEvent(ev: MotionEvent): Boolean
    val isScaling: Boolean
    fun setOnGestureListener(listener: OnGestureListener?)
}
