package com.example.slackclone.Adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.example.slackclone.Model.Message
import com.example.slackclone.R
import com.example.slackclone.Services.UserDataService
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class MessagesAdapter(val context: Context, val messages: ArrayList<Message>) : RecyclerView.Adapter<MessagesAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.message_list_view, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return messages.count()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindMessage(context, messages[position])
    }

    inner class ViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {
        val userImage = itemView?.findViewById<ImageView>(R.id.messageUserImage)
        val userName = itemView?.findViewById<TextView>(R.id.messageUserName)
        val timeStamp = itemView?.findViewById<TextView>(R.id.messageTimeStamp)
        val messageBody = itemView?.findViewById<TextView>(R.id.messageBody)

        fun bindMessage(context: Context, message: Message) {
            val resourceId = context.resources.getIdentifier(message.userAvatar, "drawable", context.packageName)
            userImage?.setImageResource(resourceId)
            userImage?.setBackgroundColor(UserDataService.getAvatarColor(message.userAvatarColor))
            userName?.text = message.userName
            timeStamp?.text = formatTimeStamp(message.timeStamp)
            messageBody?.text = message.messageBody
        }

        fun formatTimeStamp(timeStamp: String) : String {

            //2019-04-05T23:07:48.677Z
            val inputFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            inputFormatter.timeZone = TimeZone.getTimeZone("UTC")
            var formattedTimeStamp = Date()
            try {
                formattedTimeStamp = inputFormatter.parse(timeStamp)
            } catch (e: ParseException) {
                Log.d("PARSE", "EXC: ${e.localizedMessage}")
            }
            val outputFormatter = SimpleDateFormat("MMM d, yyyy, h:mm a", Locale.getDefault())
            return outputFormatter.format(formattedTimeStamp)
        }
    }
}