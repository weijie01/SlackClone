package com.example.slackclone.Services

import android.graphics.Color

object UserDataService {

    var id = ""
    var name = ""
    var email = ""
    var avatarName = ""
    var avatarColor = ""

    fun getAvatarColor(color: String): Int {

        val colorStripped = color.substring(1, color.length - 1)
        val components = colorStripped.split(",")
        val colorR = (components[0].toDouble() * 255).toInt()
        val colorG = (components[1].toDouble() * 255).toInt()
        val colorB = (components[2].toDouble() * 255).toInt()

        return Color.rgb(colorR, colorG, colorB)
    }
}