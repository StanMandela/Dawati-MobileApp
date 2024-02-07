package com.ke.dawaati.util

import android.content.Context
import android.content.Context.CONNECTIVITY_SERVICE
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Build.VERSION_CODES
import com.ke.dawaati.util.AnalyticConstants.MOBILE
import com.ke.dawaati.util.AnalyticConstants.NO_NETWORK
import com.ke.dawaati.util.AnalyticConstants.WIFI
import java.text.SimpleDateFormat
import java.util.Calendar

fun getModel(): String {
    return (
        Build.MANUFACTURER + " " + Build.MODEL + " " + Build.VERSION.RELEASE +
            " " + VERSION_CODES::class.java.fields[Build.VERSION.SDK_INT].name
        )
}

fun getCurrentTimeStamp(): String =
    SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().time)

fun getTimeStamp(): String =
    SimpleDateFormat("yyMMddHHmmss").format(Calendar.getInstance().time)

fun getNetworkType(context: Context): String {
    val manager = context.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager

    var networkType = NO_NETWORK
    val network = manager.activeNetwork
    network?.let {
        val networkCapabilities = manager.getNetworkCapabilities(network)

        networkType = if (networkCapabilities?.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) == true)
            MOBILE
        else
            WIFI
    }
    return networkType
}

object AnalyticConstants {
    const val ACCOUNT_CREATED = "Account created"
    const val ACCOUNT_LOGIN = "Account login"
    const val ACCOUNT_ACTIVATED = "Account activated"
    const val EMPTY = ""
    const val MOBILE = "Mobile"
    const val WIFI = "Wi-Fi"
    const val NO_NETWORK = "No network"
    const val RESUME_CONTENT = "Resume content"
    const val SHARE_APP = "Shared app"
    const val REVIEW_APP = "Review app"
    const val VIDEOS = "Videos"
    const val EBOOKS = "Ebooks"
    const val SESSION = "session"
}
