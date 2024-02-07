package com.ke.dawaati.util

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.provider.Settings

fun getDeviceName(): String {
    return Build.DEVICE
}

fun getDeviceType(): String {
    return "Mobile"
}

fun getDeviceVersion(): String {
    return "Android " + Build.VERSION.RELEASE
}

@SuppressLint("HardwareIds")
fun getDeviceImei(context: Context): String {
    return Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
}
