package com.example.slackclone

import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_signup.*
import java.util.*

class SignupActivity : AppCompatActivity() {

    var userAvatar = "profiledefault"
    var avatarBackgroundColor = "[0.5, 0.5, 0.5, 1]"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
    }

    fun generateAvatar(view: View) {
        val random = Random()
        val color = random.nextInt(2)
        val avatar = random.nextInt(28)

        if (color == 0) {
            userAvatar = "dark$avatar"
        }
        else {
            userAvatar = "light$avatar"
        }

        val resourceId = resources.getIdentifier(userAvatar, "drawable", packageName)
        generateAvatarButton.setImageResource(resourceId)
    }

    fun generateBackgroundColor(view: View) {
        val random = Random()
        val colorR = random.nextInt(256)
        val colorG = random.nextInt(256)
        val colorB = random.nextInt(256)

        generateAvatarButton.setBackgroundColor(Color.rgb(colorR, colorG, colorB))

        val savedR = colorR.toDouble() / 255
        val savedG = colorG.toDouble() / 255
        val savedB = colorB.toDouble() / 255

        avatarBackgroundColor = "[$savedR, $savedG, $savedB, 1]"
    }

    fun signupClicked(view: View) {

    }
}
