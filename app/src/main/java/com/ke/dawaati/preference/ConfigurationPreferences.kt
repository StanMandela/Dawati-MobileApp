package com.ke.dawaati.preference

import android.content.Context
import android.content.SharedPreferences
import org.koin.dsl.module

class ConfigurationPrefs(private val configPreferences: SharedPreferences) {
    fun setValidated(userValidated: Boolean) {
        val editor = configPreferences.edit()
        editor.putBoolean(IS_VALIDATED, userValidated)
        editor.apply()
    }

    fun getValidated(): Boolean {
        return configPreferences.getBoolean(IS_VALIDATED, false)
    }

    fun setToken(token: String) {
        val editor = configPreferences.edit()
        editor.putString(TOKEN, token)
        editor.apply()
    }

    fun getToken(): String =
        configPreferences.getString(TOKEN, "") ?: ""

    fun setLevel(levelID: String, levelName: String) {
        val editor = configPreferences.edit()
        editor.putString(LEVEL_ID, levelID)
        editor.putString(LEVEL_NAME, levelName)
        editor.apply()
    }

    fun getLevel(): Pair<String?, String?> =
        Pair(
            configPreferences.getString(LEVEL_ID, "level_001"),
            configPreferences.getString(LEVEL_NAME, "Form 1")
        )

    fun setSubject(subjectID: String, subjectName: String) {
        val editor = configPreferences.edit()
        editor.putString(SUBJECT_ID, subjectID)
        editor.putString(SUBJECT_NAME, subjectName)
        editor.apply()
    }

    fun getSubject(): Pair<String?, String?> =
        Pair(
            configPreferences.getString(SUBJECT_ID, ""),
            configPreferences.getString(SUBJECT_NAME, "")
        )

    fun setVideoDirection(direction: String) {
        val editor = configPreferences.edit()
        editor.putString(DIRECTION, direction)
        editor.apply()
    }

    fun getVideoDirection(): String? =
        configPreferences.getString(DIRECTION, "")

    fun setVideos(videoID: String) {
        val editor = configPreferences.edit()
        editor.putString(VIDEO_ID, videoID)
        editor.apply()
    }

    fun getVideos(): String =
        configPreferences.getString(VIDEO_ID, "") ?: ""

    fun setEBooks(eBookID: String) {
        val editor = configPreferences.edit()
        editor.putString(EBOOK_ID, eBookID)
        editor.apply()
    }

    fun getEBooks(): String? {
        return configPreferences.getString(EBOOK_ID, "")
    }

    fun setExam(examID: String) {
        val editor = configPreferences.edit()
        editor.putString(EXAM_ID, examID)
        editor.apply()
    }

    fun getExam(): String? {
        return configPreferences.getString(EXAM_ID, "")
    }

    companion object {
        const val TOKEN = "token"
        const val IS_VALIDATED = "is_validated"
        const val PREFS_NAME = "config_preferences"
        const val LEVEL_ID = "level_id"
        const val LEVEL_NAME = "level_name"
        const val SUBJECT_ID = "subject_id"
        const val SUBJECT_NAME = "subject_name"
        const val DIRECTION = "direction"
        const val VIDEO_ID = "video_id"
        const val EBOOK_ID = "ebook_id"
        const val EXAM_ID = "exam_id"
    }
}

val configurationModule = module {
    single { get<Context>().getSharedPreferences(ConfigurationPrefs.PREFS_NAME, Context.MODE_PRIVATE) }
    single { ConfigurationPrefs(configPreferences = get()) }
}
