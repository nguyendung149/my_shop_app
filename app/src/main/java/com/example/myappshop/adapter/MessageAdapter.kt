package com.example.myappshop.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myappshop.R
import com.example.myappshop.firestore.FirestoreClass
import com.example.myappshop.models.Messages
import com.example.myappshop.ui.activities.message.ChatLogActivity
import com.squareup.picasso.Picasso

data class MessageAdapter(var listOfImage:ArrayList<String>) : RecyclerView.Adapter<MessageHolder>() {

    private var listOfMessage = listOf<Messages>()
    private var user_image_string = " "
    private val LEFT = 0
    private val RIGHT = 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == RIGHT) {
            val view = inflater.inflate(R.layout.user_message_row_to, parent, false)
            MessageHolder(view)
        } else {
            val view = inflater.inflate(R.layout.user_message_row_from, parent, false)
            MessageHolder(view)
        }
    }

    override fun getItemCount() = listOfMessage.size

    override fun onBindViewHolder(holder: MessageHolder, position: Int) {
        val message = listOfMessage[position]

        holder.messageText.visibility = View.VISIBLE
        holder.timeOfSent.visibility = View.VISIBLE
        holder.user_image.visibility = View.VISIBLE

        Picasso.get().load(user_image_string)
            .into(holder.user_image)
        holder.messageText.text = message.message
        holder.timeOfSent.text = message.time?: ""


    }

    override fun getItemViewType(position: Int):Int {

        if (listOfMessage[position].sender == FirestoreClass().getCurrentUserId()) {
            user_image_string = listOfImage[0]
            return RIGHT
        } else {
            user_image_string = listOfImage[1]
            return LEFT
        }
    }

    fun setList(newList: List<Messages>) {
        this.listOfMessage = newList
    }

}

class MessageHolder(itemView: View) : RecyclerView.ViewHolder(itemView.rootView) {
    val user_image : ImageView = itemView.findViewById(R.id.message_user_icon)
    val messageText: TextView = itemView.findViewById(R.id.content_message)
    val timeOfSent: TextView = itemView.findViewById(R.id.tv_time)
}

