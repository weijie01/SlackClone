package com.example.slackclone.Services

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.support.v4.content.LocalBroadcastManager
import com.example.slackclone.Utilities.BROADCAST_USER_DATA_CHANGE

object UserDataService {

    var id = ""
    var name = ""
    var email = ""
    var avatarName = ""
    var avatarColor = ""

    fun setUserData(context: Context, id: String, name: String, email: String, avatarName: String, avatarColor: String) {

        this.id = id
        this.name = name
        this.email = email
        this.avatarName = avatarName
        this.avatarColor = avatarColor

        val userDataChange = Intent(BROADCAST_USER_DATA_CHANGE)
        LocalBroadcastManager.getInstance(context).sendBroadcast(userDataChange)

    }

    fun getAvatarColor(color: String): Int {

        val colorStripped = color.substring(1, color.length - 1)
        val components = colorStripped.split(",")
        val colorR = (components[0].toDouble() * 255).toInt()
        val colorG = (components[1].toDouble() * 255).toInt()
        val colorB = (components[2].toDouble() * 255).toInt()

        return Color.rgb(colorR, colorG, colorB)
    }

    fun clear() {
        id = ""
        name = ""
        email = ""
        avatarName = ""
        avatarColor = ""
    }
}