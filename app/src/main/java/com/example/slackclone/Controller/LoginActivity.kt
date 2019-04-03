package com.example.slackclone.Controller

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.slackclone.R
import com.example.slackclone.Services.AuthService
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        setSendingRequestStatus(false)
    }

    fun loginClicked(view: View) {
        val email = emailTextLogin.text.toString()
        val password = passwordTextLogin.text.toString()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Make sure you have filled in each field.", Toast.LENGTH_LONG).show()
            return
        }

        setSendingRequestStatus(true)
        AuthService.loginUser(this, email, password) {loginSuccess ->
            if (loginSuccess) {
                AuthService.findUser(this, email) {findSuccess ->
                    if (findSuccess) {
                        setSendingRequestStatus(false)
                        finish()
                    }
                    else {
                        Toast.makeText(this, "Fail to login. Please try again.", Toast.LENGTH_LONG).show()
                        setSendingRequestStatus(false)
                    }
                }
            }
            else {
                Toast.makeText(this, "Fail to login. Please try again.", Toast.LENGTH_LONG).show()
                setSendingRequestStatus(false)
            }
        }
    }

    fun signupClicked(view: View) {
        val signupIntent = Intent(this, SignupActivity::class.java)
        startActivity(signupIntent)
        finish()
    }

    fun setSendingRequestStatus(isSending: Boolean) {
        if (isSending) {
            progressBarLogin.visibility = View.VISIBLE
            loginButtonLogin.isEnabled = false
            signupButtonLogin.isEnabled = false
        }
        else {
            progressBarLogin.visibility = View.INVISIBLE
            loginButtonLogin.isEnabled = true
            signupButtonLogin.isEnabled = true
        }
    }
}
