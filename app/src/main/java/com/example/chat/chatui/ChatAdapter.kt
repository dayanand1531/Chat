package com.example.chat.chatui

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chat.R
import com.example.chat.map.ReceviedMapViewHolder
import com.example.chat.map.SendMapViewHolder
import com.example.chat.util.MessageType

class ChatAdapter(
    private val messages: List<com.example.chat.api.model.entity.Message>,
    private val context: Context
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun getItemViewType(position: Int): Int {
        val isSend = messages[position].isSent
        return when (messages[position].messageType) {
            MessageType.TEXT.value -> {
                if (!isSend) R.layout.item_recevied_message
                else R.layout.item_send_message
            }

            MessageType.IMAGE.value -> {
                if (!isSend) R.layout.item_recevied_image
                else R.layout.item_send_image
            }

            MessageType.LOCATION.value -> if (!isSend) R.layout.item_recevied_location else R.layout.item_send_location

            else -> {
                Log.d("TAG", "getItemViewType: not found")
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(viewType, parent, false)
        return when (viewType) {
            R.layout.item_send_message -> MessageSendViewHolder(view)
            R.layout.item_recevied_message -> MessageReceviedViewHolder(view)
            R.layout.item_recevied_image -> ImageReceviedViewHolder(view)
            R.layout.item_send_image -> ImageSendViewHolder(view)
            R.layout.item_recevied_location -> ReceviedMapViewHolder(view)
            R.layout.item_send_location -> SendMapViewHolder(view)
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is MessageSendViewHolder -> holder.bind(messages[position])
            is MessageReceviedViewHolder -> holder.bind(messages[position])
            is ImageReceviedViewHolder -> holder.bind(messages[position])
            is ImageSendViewHolder -> holder.bind(messages[position])
            is SendMapViewHolder -> holder.bind(context, messages[position])
            is ReceviedMapViewHolder ->holder.bind(context,messages[position])
        }
    }

    override fun getItemCount(): Int = messages.size
}

// ViewHolders for each type of message
class MessageSendViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    fun bind(message: com.example.chat.api.model.entity.Message) {
        val messages = message.message
        val text = itemView.findViewById<TextView>(R.id.tvSentMessage)
        text.text = messages
    }
}

class MessageReceviedViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    fun bind(message: com.example.chat.api.model.entity.Message) {
        val messages = message.message
        val text = itemView.findViewById<TextView>(R.id.tvReveviedMessage)
        text.text = messages
    }
}

class ImageSendViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    fun bind(message: com.example.chat.api.model.entity.Message) {
        val image = itemView.findViewById<ImageView>(R.id.ivSentImg)
        if (message.isSent)  Glide.with(itemView.context).load(message.imageUrl).into(image)
        else image.setImageResource(R.drawable.ic_image_24)
    }
}

class ImageReceviedViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    fun bind(message: com.example.chat.api.model.entity.Message) {
        val image = itemView.findViewById<ImageView>(R.id.ivImg)
       if (message.imageUrl!=null) Glide.with(itemView.context).load(message.imageUrl).into(image)
       else image.setImageResource(R.drawable.ic_image_24)
    }
}


