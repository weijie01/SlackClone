package com.example.slackclone.Controller

import android.app.Application
import com.example.slackclone.Utilities.SharedPref

class App : Application() {

    companion object {
        lateinit var prefs: SharedPref
    }

    override fun onCreate() {
        prefs = SharedPref(applicationContext)
        super.onCreate()
    }
}