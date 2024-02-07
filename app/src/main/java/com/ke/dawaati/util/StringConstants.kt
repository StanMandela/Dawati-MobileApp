package com.ke.dawaati.util

import com.ke.dawaati.util.StringConstants.PROFILE_DATE_DISPLAY
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object StringConstants {
    const val SECTIONED = "sectioned"
    const val NORMAL = "normal"
    const val PROFILE_DATE_DISPLAY = "dd MMMM, YYYY"
}

fun String.formatToDisplayDate(displayFormat: String = PROFILE_DATE_DISPLAY): String {
    var dateFormat = when {
        matches("\\d{4}-\\d{2}-\\d{2}".toRegex()) ->
            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        matches("\\d{4}/\\d{2}/\\d{2}".toRegex()) ->
            SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
        matches("\\d{2}-\\d{2}-\\d{4}".toRegex()) ->
            SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        matches("\\d{2}/\\d{2}/\\d{4}".toRegex()) ->
            SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}".toRegex()) ->
            SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        matches("\\d{4}/\\d{2}/\\d{2} \\d{2}:\\d{2}:\\d{2}".toRegex()) ->
            SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault())
        matches("\\d{1,2}/\\d{1,2}/\\d{4} \\d{1,2}:\\d{2}:\\d{2} \\w{2}".toRegex()) ->
            SimpleDateFormat("MM/dd/yyyy HH:mm:ss a", Locale.getDefault())
        matches("\\d{2}/\\d{2}/\\d{4} \\d{2}:\\d{2}:\\d{2}".toRegex()) ->
            SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault())
        matches("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}.\\d{3}".toRegex()) ->
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.getDefault())
        else ->
            SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault())
    }

    return try {
        val date: Date = dateFormat.parse(this) as Date
        dateFormat = SimpleDateFormat(displayFormat, Locale.getDefault())
        dateFormat.format(date)
    } catch (exception: ParseException) {
        this
    }
}
