package com.example.slackclone.Services

import android.content.Context
import android.util.Log
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.slackclone.Controller.App
import com.example.slackclone.Utilities.URL_CREATE_USER
import com.example.slackclone.Utilities.URL_FIND_USER
import com.example.slackclone.Utilities.URL_LOGIN
import com.example.slackclone.Utilities.URL_REGISTER
import org.json.JSONException
import org.json.JSONObject

object AuthService {

    fun clear() {
        App.prefs.isLoggedIn = false
        App.prefs.email = ""
        App.prefs.authToken = ""
    }

    fun registerUser(context: Context, email: String, password: String, complete: (Boolean) -> Unit) {

        val jsonBody = JSONObject()
        jsonBody.put("email", email)
        jsonBody.put("password", password)

        val registerRequest = object: StringRequest(Method.POST, URL_REGISTER, Response.Listener{response ->

            complete(true)

        }, Response.ErrorListener {error ->

            Log.d("Error", "Cannot register user: $error")
            complete(false)

        }) {
            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }

            override fun getBody(): ByteArray {
                return jsonBody.toString().toByteArray()
            }
        }

        App.prefs.requestQueue.add(registerRequest)
    }

    fun loginUser(context: Context, email: String, password: String, complete: (Boolean) -> Unit) {

        val jsonBody = JSONObject()
        jsonBody.put("email", email)
        jsonBody.put("password", password)

        val loginRequest = object: JsonObjectRequest(Method.POST, URL_LOGIN, null, Response.Listener {response ->

            try {

                App.prefs.isLoggedIn = true
                App.prefs.email = response.getString("user")
                App.prefs.authToken = response.getString("token")
                complete(true)

            } catch(e: JSONException) {

                Log.d("JSON", "EXC: ${e.localizedMessage}")
                complete(false)

            }

        }, Response.ErrorListener { error ->

            Log.d("ERROR", "Cannot login user: $error")
            complete(false)

        }) {
            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }

            override fun getBody(): ByteArray {
                return jsonBody.toString().toByteArray()
            }
        }

        App.prefs.requestQueue.add(loginRequest)
    }

    fun createUser(context: Context, name: String, email: String, avatarName: String, avatarColor: String, complete: (Boolean) -> Unit) {

        val jsonBody = JSONObject()
        jsonBody.put("name", name)
        jsonBody.put("email", email)
        jsonBody.put("avatarName", avatarName)
        jsonBody.put("avatarColor", avatarColor)

        val createRequest = object: JsonObjectRequest(Method.POST, URL_CREATE_USER, null, Response.Listener { response ->

            try {

                UserDataService.setUserData(context, response.getString("_id"), response.getString("name"),
                    response.getString("email"), response.getString("avatarName"),
                    response.getString("avatarColor"))
                complete(true)

            } catch(e: JSONException) {

                Log.d("JSON", "EXC: ${e.localizedMessage}")
                complete(false)

            }

        }, Response.ErrorListener { error ->

            Log.d("ERROR", "Cannot create user: $error")
            complete(false)

        }) {
            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }

            override fun getBody(): ByteArray {
                return jsonBody.toString().toByteArray()
            }

            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers.put("Authorization", "Bearer ${App.prefs.authToken}")
                return headers
            }
        }

        App.prefs.requestQueue.add(createRequest)
    }

    fun findUser(context: Context, email: String, complete: (Boolean) -> Unit) {
        val findRequest = object: JsonObjectRequest(Method.GET, "$URL_FIND_USER$email", null, Response.Listener { response ->

            try {

                UserDataService.setUserData(context, response.getString("_id"), response.getString("name"),
                    response.getString("email"), response.getString("avatarName"),
                    response.getString("avatarColor"))
                complete(true)

            } catch(e: JSONException) {

                Log.d("JSON", "EXC: ${e.localizedMessage}")
                complete(false)

            }

        }, Response.ErrorListener { error ->

            Log.d("ERROR", "Cannot find user: $error")
            complete(false)

        }) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers.put("Authorization", "Bearer ${App.prefs.authToken}")
                return headers
            }
        }

        App.prefs.requestQueue.add(findRequest)
    }
}