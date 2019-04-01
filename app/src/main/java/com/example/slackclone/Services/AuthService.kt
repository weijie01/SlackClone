package com.example.slackclone.Services

import android.content.Context
import android.util.Log
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.slackclone.Utilities.URL_LOGIN
import com.example.slackclone.Utilities.URL_REGISTER
import org.json.JSONException
import org.json.JSONObject

object AuthService {

    var isLoggedIn = false
    var email = ""
    var authToken = ""

    fun registerUser(context: Context, email: String, password: String, complete: (Boolean) -> Unit) {

        val jsonBody = JSONObject()
        jsonBody.put("email", email)
        jsonBody.put("password", password)

        val registerRequest = object: StringRequest(Method.POST, URL_REGISTER, Response.Listener{response ->

            println(response)
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

        Volley.newRequestQueue(context).add(registerRequest)
    }

    fun loginUser(context: Context, email: String, password: String, complete: (Boolean) -> Unit) {

        val jsonBody = JSONObject()
        jsonBody.put("email", email)
        jsonBody.put("password", password)

        val loginRequest = object: JsonObjectRequest(Method.POST, URL_LOGIN, null, Response.Listener {response ->

            try {
                isLoggedIn = true
                this.email = response.getString("user")
                authToken = response.getString("token")
                complete(true)
            } catch(e: JSONException) {
                Log.d("JSON", "EXC: ${e.localizedMessage}")
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

        Volley.newRequestQueue(context).add(loginRequest)
    }
}