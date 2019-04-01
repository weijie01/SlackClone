package com.example.slackclone.Controller

import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.slackclone.R
import com.example.slackclone.Services.AuthService
import kotlinx.android.synthetic.main.activity_signup.*
import java.util.*

class SignupActivity : AppCompatActivity() {

    var avatarName = "profiledefault"
    var avatarColor = "[0.5, 0.5, 0.5, 1]"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
    }

    fun generateAvatar(view: View) {
        val random = Random()
        val avatarGroup = random.nextInt(2)
        val avatarIndex = random.nextInt(28)

        if (avatarGroup == 0) {
            avatarName = "dark$avatarIndex"
        }
        else {
            avatarName = "light$avatarIndex"
        }

        val resourceId = resources.getIdentifier(avatarName, "drawable", packageName)
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

        avatarColor = "[$savedR, $savedG, $savedB, 1]"
    }

    fun signupClicked(view: View) {
        val name = usernameText.text.toString()
        val email = emailText.text.toString()
        val password = passwordText.text.toString()

        AuthService.registerUser(this, email, password) { registerSuccess ->
            if (registerSuccess) {
                AuthService.loginUser(this, email, password) { loginSuccess ->
                    if (loginSuccess) {
                        AuthService.createUser(this, name, email, avatarName, avatarColor) { createSuccess ->
                            if (createSuccess) {
                                finish()
                            }
                        }
                    }
                }
            }
        }
    }
}
