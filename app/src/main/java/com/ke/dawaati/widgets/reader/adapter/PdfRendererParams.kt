/*
 * Copyright (C) 2016 Olmo Gallegos Hernández.
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
package com.ke.dawaati.widgets.reader.adapter

import android.graphics.Bitmap

class PdfRendererParams {
    var width = 0
    var height = 0
    var renderQuality = 0f
    var offScreenSize = 0
    var config = DEFAULT_CONFIG

    companion object {
        private val DEFAULT_CONFIG = Bitmap.Config.ARGB_8888
    }
}
