package com.ke.dawaati.widgets.reader.gestures

import android.content.Context
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES

/*******************************************************************************
 * Copyright 2011, 2012 Chris Banes.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
object VersionedGestureDetector {
    fun newInstance(
        context: Context?,
        listener: OnGestureListener?
    ): GestureDetector {
        val sdkVersion = VERSION.SDK_INT
        val detector: GestureDetector = if (sdkVersion < VERSION_CODES.ECLAIR) {
            CupcakeGestureDetector(context)
        } else if (sdkVersion < VERSION_CODES.FROYO) {
            EclairGestureDetector(context)
        } else {
            FroyoGestureDetector(context)
        }
        detector.setOnGestureListener(listener)
        return detector
    }
}