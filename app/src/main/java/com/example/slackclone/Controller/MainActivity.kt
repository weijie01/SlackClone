package com.example.slackclone.Controller

import android.content.*
import android.graphics.Color
import android.os.Bundle
import android.support.v4.content.LocalBroadcastManager
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.TextView
import com.example.slackclone.Adapters.MessagesAdapter
import com.example.slackclone.Model.Channel
import com.example.slackclone.Model.Message
import com.example.slackclone.R
import com.example.slackclone.Services.AuthService
import com.example.slackclone.Services.MessageService
import com.example.slackclone.Services.UserDataService
import com.example.slackclone.Utilities.BROADCAST_USER_DATA_CHANGE
import com.example.slackclone.Utilities.SOCKET_URL
import io.socket.client.IO
import io.socket.emitter.Emitter
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.nav_header_main.*

class MainActivity : AppCompatActivity() {

    val socket = IO.socket(SOCKET_URL)
    lateinit var channelsAdapter : ArrayAdapter<Channel>
    lateinit var messagesAdapter : MessagesAdapter
    var selectedChannel : Channel? = null

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
        socket.on("channelCreated", onNewChannel)
        socket.on("messageCreated", onNewMessage)

        channelsAdapter = ArrayAdapter<Channel>(this, android.R.layout.simple_list_item_1, MessageService.channels)
        channel_list.adapter = channelsAdapter

        channel_list.setOnItemClickListener { adapterView, view, i, l ->
            selectedChannel = MessageService.channels[i]
            updateWithChannel()
            drawer_layout.closeDrawer(GravityCompat.START)
        }

        messagesAdapter = MessagesAdapter(this, MessageService.messages)
        messageList.adapter = messagesAdapter
        messageList.layoutManager = LinearLayoutManager(this)

        if (App.prefs.isLoggedIn) {
            AuthService.findUser(this, App.prefs.email) {}
        }
    }

    private val onNewChannel = Emitter.Listener { args ->
        if (App.prefs.isLoggedIn) {
            runOnUiThread {
                val channelName = args[0] as String
                val channelDesc = args[1] as String
                val channelId = args[2] as String

                val newChannel = Channel(channelName, channelDesc, channelId)
                MessageService.channels.add(newChannel)
                channelsAdapter.notifyDataSetChanged()
            }
        }
    }

    private val onNewMessage = Emitter.Listener { args ->
        if (App.prefs.isLoggedIn) {
            runOnUiThread {
                val channelId = args[2] as String
                if (channelId == selectedChannel?.id) {
                    val messageBody = args[0] as String
                    val userId = args[1] as String

                    val userName = args[3] as String
                    val userAvatar = args[4] as String
                    val userAvatarColor = args[5] as String
                    val messageId = args[6] as String
                    val timeStamp = args[7] as String

                    val newMessage = Message(messageBody, userId, channelId, userName, userAvatar, userAvatarColor, messageId, timeStamp)
                    MessageService.messages.add(newMessage)

                    //adapter notify change
                    messagesAdapter.notifyDataSetChanged()
                }
            }
        }
    }

    override fun onDestroy() {
        socket.disconnect()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(userDataChangeReceiver)
        super.onDestroy()
    }

    private val userDataChangeReceiver = object: BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent?) {

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

            MessageService.getChannels(context) { getChannelsSuccess ->
                if (getChannelsSuccess) {
                    channelsAdapter.notifyDataSetChanged()

                    if (MessageService.channels.count() > 0) {
                        selectedChannel = MessageService.channels[0]
                        updateWithChannel()
                    }
                }
            }
        }
    }

    fun updateWithChannel() {
        selectedChannelName.text = "#${selectedChannel?.name}"

        //download messages of the selected channel
        if (selectedChannel != null) {
            MessageService.getMessages(this, selectedChannel!!.id) { getMessagesSuccess ->
                if (getMessagesSuccess) {

                    //DEBUG: print each message
                    for (message in MessageService.messages) {
                        println(message.messageBody)
                    }

                    messagesAdapter.notifyDataSetChanged()
                }
            }
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
        if (App.prefs.isLoggedIn) {
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
        if (App.prefs.isLoggedIn) {

            AuthService.clear()
            UserDataService.clear()
            MessageService.clear()

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

        if (!App.prefs.isLoggedIn || messageText.text.isEmpty() || selectedChannel == null) {
            return
        }

        val messageBody = messageText.text.toString()
        val userId = UserDataService.id
        val channelId = selectedChannel!!.id
        val userName = UserDataService.name
        val userAvatar = UserDataService.avatarName
        val userAvatarColor = UserDataService.avatarColor

        socket.emit("newMessage", messageBody, userId, channelId, userName, userAvatar, userAvatarColor)

        messageText.text.clear()
    }

    fun hideKeyBoard() {
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        if (inputMethodManager.isAcceptingText) {
            inputMethodManager.hideSoftInputFromWindow(currentFocus.windowToken, 0)
        }
    }
}
