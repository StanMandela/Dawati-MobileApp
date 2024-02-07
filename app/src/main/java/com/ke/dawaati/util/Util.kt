package com.ke.dawaati.util

import android.app.Activity
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.res.Resources

fun toTitleCase(str: String?, handleWhiteSpace: Boolean): String {
    if (str == null) {
        return ""
    }
    var space = true
    val builder = StringBuilder(str)
    val len = builder.length
    for (i in 0 until len) {
        val c = builder[i]
        if (space) {
            if (!Character.isWhitespace(c)) {
                // Convert to title case and switch out of whitespace mode.
                builder.setCharAt(i, Character.toTitleCase(c))
                space = false
            }
        } else if (Character.isWhitespace(c)) {
            space = handleWhiteSpace
        } else {
            builder.setCharAt(i, Character.toLowerCase(c))
        }
    }
    return builder.toString()
}

fun String?.camelCase(ignoreWhiteSpace: Boolean = true): String {
    this?.let { string ->
        var isCharSpace = true
        val stringBuilder = StringBuilder(string)
        repeat(times = stringBuilder.length) { index ->
            val c = stringBuilder[index]
            if (isCharSpace) {
                if (!Character.isWhitespace(c)) {
                    // Convert to title case and switch out of whitespace mode.
                    stringBuilder.setCharAt(index, Character.toTitleCase(c))
                    isCharSpace = false
                }
            } else if (Character.isWhitespace(c)) {
                isCharSpace = ignoreWhiteSpace
            } else {
                stringBuilder.setCharAt(index, Character.toLowerCase(c))
            }
        }
        return stringBuilder.toString()
    } ?: run {
        return ""
    }
}

fun String?.getInitials(): String {
    var initials = ""

    this?.let {
        // Clear unnecessary white spaces i.e Kelvin   Kioko to Kelvin Kioko
        val name = it.trim().replace("\\s+".toRegex(), " ")
        val nameArray = name.split(" ")
        initials = when (nameArray.size) {
            1 -> nameArray[0].firstChar()
            2 -> "${nameArray[0].firstChar()}${nameArray[1].firstChar()}"
            else -> "${nameArray[0].firstChar()}${nameArray[1].firstChar()}"
        }
        return initials
    } ?: run {
        return initials
    }
}

// Splitting a string with white space " " creates an array with 1 empty string
// If we try to get the first char it will crash because string index out of reach or something of the sort
fun String.firstChar(): String = if (this.isBlank() || this.isEmpty()) "" else this[0].toString()

fun setupAppVersion(activity: Activity): String {
    val pm = activity.packageManager
    var pInfo: PackageInfo? = null
    try {
        pInfo = pm.getPackageInfo(activity.packageName, 0)
    } catch (e1: PackageManager.NameNotFoundException) {
        e1.printStackTrace()
    }
    return pInfo!!.versionName
}

val Int.dp: Int
    get() = (this * Resources.getSystem().displayMetrics.density + 0.5f).toInt()

val Float.dp: Int
    get() = (this * Resources.getSystem().displayMetrics.density + 0.5f).toInt()
