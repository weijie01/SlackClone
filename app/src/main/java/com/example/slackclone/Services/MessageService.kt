package com.example.slackclone.Services

import android.content.Context
import android.util.Log
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.example.slackclone.Controller.App
import com.example.slackclone.Model.Channel
import com.example.slackclone.Model.Message
import com.example.slackclone.Utilities.URL_GET_CHANNELS
import com.example.slackclone.Utilities.URL_GET_MESSAGES
import org.json.JSONException

object MessageService {

    val channels = ArrayList<Channel>()
    val messages = ArrayList<Message>()

    fun clear() {
        channels.clear()
        messages.clear()
    }

    fun getChannels(context: Context, complete: (Boolean) -> Unit) {
        val channelsRequest = object: JsonArrayRequest(Method.GET, URL_GET_CHANNELS, null, Response.Listener { response ->

            try {
                for (i in 0 until response.length()) {
                    val channelJsonObject = response.getJSONObject(i)
                    val channelName = channelJsonObject.getString("name")
                    val channelDesc = channelJsonObject.getString("description")
                    val channelId = channelJsonObject.getString("_id")

                    val newChannel = Channel(channelName, channelDesc, channelId)
                    channels.add(newChannel)
                }
                complete(true)
            } catch (e: JSONException) {
                Log.d("JSON", "EXC: ${e.localizedMessage}")
                complete(false)
            }

        }, Response.ErrorListener { error ->
            Log.d("ERROR", "Cannot get channels: $error")
            complete(false)
        }) {
            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }

            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers.put("Authorization", "Bearer ${App.prefs.authToken}")
                return headers
            }
        }

        App.prefs.requestQueue.add(channelsRequest)
    }

    fun getMessages(context: Context, channelId: String, complete: (Boolean) -> Unit) {
        messages.clear()

        val messagesRequest = object: JsonArrayRequest(Method.GET, "${URL_GET_MESSAGES}$channelId", null, Response.Listener { response ->
            try {
                for (i in 0 until response.length()) {
                    val messageJsonObject = response.getJSONObject(i)
                    val messageBody = messageJsonObject.getString("messageBody")
                    val userId = messageJsonObject.getString("userId")
                    val channelId = messageJsonObject.getString("channelId")
                    val userName = messageJsonObject.getString("userName")
                    val userAvatar = messageJsonObject.getString("userAvatar")
                    val userAvatarColor = messageJsonObject.getString("userAvatarColor")
                    val id = messageJsonObject.getString("_id")
                    val timeStamp = messageJsonObject.getString("timeStamp")

                    val newMessage = Message(messageBody, userId, channelId, userName, userAvatar, userAvatarColor, id, timeStamp)
                    messages.add(newMessage)
                }
                complete(true)

            } catch (e: JSONException) {
                Log.d("JSON", "EXC: ${e.localizedMessage}")
                complete(false)
            }

        }, Response.ErrorListener { error ->
            Log.d("ERROR", "Cannot get messages: $error")
            complete(false)
        }) {
            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }

            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers.put("Authorization", "Bearer ${App.prefs.authToken}")
                return headers
            }
        }

        App.prefs.requestQueue.add(messagesRequest)
    }
}