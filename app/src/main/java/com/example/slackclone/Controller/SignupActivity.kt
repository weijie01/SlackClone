package com.example.slackclone.Controller

import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.slackclone.R
import com.example.slackclone.Services.AuthService
import kotlinx.android.synthetic.main.activity_signup.*
import java.util.*

class SignupActivity : AppCompatActivity() {

    var avatarName = "profiledefault"
    var avatarColor = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        setSendingRequestStatus(false)
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
        val name = usernameTextSignup.text.toString()
        val email = emailTextSignup.text.toString()
        val password = passwordTextSignup.text.toString()

        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Make sure you have filled in each field.", Toast.LENGTH_LONG).show()
            return
        }

        setSendingRequestStatus(true)

        AuthService.registerUser(this, email, password) { registerSuccess ->
            if (registerSuccess) {
                AuthService.loginUser(this, email, password) { loginSuccess ->
                    if (loginSuccess) {
                        AuthService.createUser(this, name, email, avatarName, avatarColor) { createSuccess ->
                            if (createSuccess) {
                                finish()
                            }
                            else {
                                Toast.makeText(this, "Fail to sign up. Please try again.", Toast.LENGTH_LONG).show()
                                setSendingRequestStatus(false)
                            }
                        }
                    }
                    else {
                        Toast.makeText(this, "Fail to sign up. Please try again.", Toast.LENGTH_LONG).show()
                        setSendingRequestStatus(false)
                    }
                }
            }
            else {
                Toast.makeText(this, "Fail to sign up. Please try again.", Toast.LENGTH_LONG).show()
                setSendingRequestStatus(false)
            }
        }
    }

    fun setSendingRequestStatus(isSending: Boolean) {
        if (isSending) {
            progressBarSignup.visibility = View.VISIBLE
            generateAvatarButton.isEnabled = false
            generateBackgroundColorButton.isEnabled = false
            signupButtonSignup.isEnabled = false
        }
        else {
            progressBarSignup.visibility = View.INVISIBLE
            generateAvatarButton.isEnabled = true
            generateBackgroundColorButton.isEnabled = true
            signupButtonSignup.isEnabled = true
        }
    }
}
