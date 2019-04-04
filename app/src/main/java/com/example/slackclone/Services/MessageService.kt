package com.example.slackclone.Services

import android.content.Context
import android.util.Log
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.example.slackclone.Model.Channel
import com.example.slackclone.Utilities.URL_GET_CHANNELS
import org.json.JSONException

object MessageService {

    val channels = ArrayList<Channel>()

    fun getChannels(context: Context, complete: (Boolean) -> Unit) {
        val channelsRequest = object: JsonArrayRequest(Method.GET, URL_GET_CHANNELS, null, Response.Listener { response ->

            try {
                for (i in 0 until response.length()) {
                    val channelJsonObject = response.getJSONObject(i)
                    val channelName = channelJsonObject.getString("name")
                    val channelDesc = channelJsonObject.getString("description")
                    val channelId = channelJsonObject.getString("_id")

                    val newChannel = Channel(channelName, channelDesc, channelId)
                    MessageService.channels.add(newChannel)
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
                headers.put("Authorization", "Bearer ${AuthService.authToken}")
                return headers
            }
        }

        Volley.newRequestQueue(context).add(channelsRequest)
    }
}