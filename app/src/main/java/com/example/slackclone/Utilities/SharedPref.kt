package com.example.slackclone.Utilities

import android.content.Context

class SharedPref(context: Context) {

    val PREFS_FILENAME = "prefs"
    val prefs = context.getSharedPreferences(PREFS_FILENAME, Context.MODE_PRIVATE)

    val IS_LOGGED_IN = "IS_LOGGED_IN"
    val EMAIL = "EMAIL"
    val AUTH_TOKEN = "AUTH_TOKEN"

    var isLoggedIn: Boolean
        get() = prefs.getBoolean(IS_LOGGED_IN, false)
        set(value) = prefs.edit().putBoolean(IS_LOGGED_IN, value).apply()

    var email: String
        get() = prefs.getString(EMAIL, "")
        set(value) = prefs.edit().putString(EMAIL, value).apply()

    var authToken: String
        get() = prefs.getString(AUTH_TOKEN, "")
        set(value) = prefs.edit().putString(AUTH_TOKEN, value).apply()
}