package com.example.slackclone.Controller

import android.content.*
import android.graphics.Color
import android.os.Bundle
import android.support.v4.content.LocalBroadcastManager
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import com.example.slackclone.R
import com.example.slackclone.Services.AuthService
import com.example.slackclone.Services.UserDataService
import com.example.slackclone.Utilities.BROADCAST_USER_DATA_CHANGE
import com.example.slackclone.Utilities.SOCKET_URL
import io.socket.client.IO
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.nav_header_main.*

class MainActivity : AppCompatActivity() {

    val socket = IO.socket(SOCKET_URL)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        LocalBroadcastManager.getInstance(this).registerReceiver(userDataChangeReceiver, IntentFilter(
            BROADCAST_USER_DATA_CHANGE))

        socket.connect()
    }

    private val userDataChangeReceiver = object: BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {

            userNameNavHeader.text = UserDataService.name
            userEmailNavHeader.text = UserDataService.email

            if (UserDataService.avatarName != "profiledefault") {
                val resourceId = resources.getIdentifier(UserDataService.avatarName, "drawable", packageName)
                userImageNavHeader.setImageResource(resourceId)
            }
            if (UserDataService.avatarColor != "") {
                userImageNavHeader.setBackgroundColor(UserDataService.getAvatarColor(UserDataService.avatarColor))
            }


            loginButtonNavHeader.text = "Logout"
        }
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    fun addChannelClicked(view: View) {
        if (AuthService.isLoggedIn) {
            val dialogView = layoutInflater.inflate(R.layout.add_channel_dialog, null)
            val builder = AlertDialog.Builder(this)
            builder.setView(dialogView)
                .setPositiveButton("Add") { dialogInterface, i ->
                    val addChannelNameText = dialogView.findViewById<EditText>(R.id.addChannelNameText)
                    val addChannelDescText = dialogView.findViewById<EditText>(R.id.addChannelDescText)
                    val channelName = addChannelNameText.text.toString()
                    val channelDesc = addChannelDescText.text.toString()

                    socket.emit("newChannel", channelName, channelDesc)
                }
                .setNegativeButton("Cancel") { dialogInterface, i ->

                }
                .show()
        }
    }

    fun loginClicked(view: View) {
        if (AuthService.isLoggedIn) {

            AuthService.clear()
            UserDataService.clear()

            userNameNavHeader.text = ""
            userEmailNavHeader.text = ""
            userImageNavHeader.setImageResource(R.drawable.profiledefault)
            userImageNavHeader.setBackgroundColor(Color.TRANSPARENT)
            loginButtonNavHeader.text = "Login"

        }
        else {
            val loginIntent = Intent(this, LoginActivity::class.java)
            startActivity(loginIntent)
        }
    }

    fun sendMessage(view: View) {

        hideKeyBoard()
    }

    fun hideKeyBoard() {
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        if (inputMethodManager.isAcceptingText) {
            inputMethodManager.hideSoftInputFromWindow(currentFocus.windowToken, 0)
        }
    }
}
