package com.example.chat.chatui

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chat.R
import com.example.chat.api.model.entity.Message
import com.example.chat.databinding.ItemReceviedImageBinding
import com.example.chat.databinding.ItemReceviedLocationBinding
import com.example.chat.databinding.ItemReceviedMessageBinding
import com.example.chat.databinding.ItemSendImageBinding
import com.example.chat.databinding.ItemSendLocationBinding
import com.example.chat.databinding.ItemSendMessageBinding
import com.example.chat.map.ReceviedMapViewHolder
import com.example.chat.map.SendMapViewHolder
import com.example.chat.util.MessageType

class ChatAdapter(
    private val context: Context
) : ListAdapter<Message, RecyclerView.ViewHolder>(MessageItemDiffCallback()) {

    override fun getItemViewType(position: Int): Int {
        val message = getItem(position)
        return when (message.messageType) {
            MessageType.TEXT.value -> {
                if (!message.isSent) R.layout.item_recevied_message
                else R.layout.item_send_message
            }

            MessageType.IMAGE.value -> {
                if (!message.isSent) R.layout.item_recevied_image
                else R.layout.item_send_image
            }

            MessageType.LOCATION.value ->
                if (!message.isSent) R.layout.item_recevied_location else R.layout.item_send_location

            else -> {
                throw IllegalStateException("Invalid view type")
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemSendMessageBinding =
            ItemSendMessageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val itemReceviedMessageBinding =
            ItemReceviedMessageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val itemReceviedImageBinding =
            ItemReceviedImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val itemSendImageBinding =
            ItemSendImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val itemSendLocationBinding =
            ItemSendLocationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val itemReceviedLocationBinding =
            ItemReceviedLocationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return when (viewType) {
            R.layout.item_send_message -> MessageSendViewHolder(itemSendMessageBinding)
            R.layout.item_recevied_message -> MessageReceviedViewHolder(itemReceviedMessageBinding)
            R.layout.item_recevied_image -> ImageReceviedViewHolder(itemReceviedImageBinding)
            R.layout.item_send_image -> ImageSendViewHolder(itemSendImageBinding)
            R.layout.item_recevied_location -> ReceviedMapViewHolder(itemReceviedLocationBinding)
            R.layout.item_send_location -> SendMapViewHolder(itemSendLocationBinding)
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is MessageSendViewHolder -> holder.bind(getItem(position))
            is MessageReceviedViewHolder -> holder.bind(getItem(position))
            is ImageReceviedViewHolder -> holder.bind(getItem(position))
            is ImageSendViewHolder -> holder.bind(getItem(position))
            is SendMapViewHolder -> holder.bind(context, getItem(position))
            is ReceviedMapViewHolder -> holder.bind(context, getItem(position))
        }
    }

}

// ViewHolders for each type of message
class MessageSendViewHolder(val binding: ItemSendMessageBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(message: Message) {
        binding.message = message
    }
}

class MessageReceviedViewHolder(val binding: ItemReceviedMessageBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(message: Message) {
        binding.message = message
    }
}

class ImageSendViewHolder(val binding: ItemSendImageBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(message: Message) {
        binding.message = message
        if (message.isSent) Glide.with(itemView.context).load(message.imageUrl)
            .into(binding.ivSentImg)
        else binding.ivSentImg.setImageResource(R.drawable.ic_image_24)
    }
}

class ImageReceviedViewHolder(val binding: ItemReceviedImageBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(message: Message) {
        binding.message = message
        if (message.imageUrl != null) Glide.with(itemView.context).load(message.imageUrl)
            .into(binding.ivImg)
        else binding.ivImg.setImageResource(R.drawable.ic_image_24)
    }
}

class MessageItemDiffCallback : DiffUtil.ItemCallback<Message>() {
    override fun areItemsTheSame(oldItem: Message, newItem: Message): Boolean {
        return oldItem.messageId == newItem.messageId
    }

    override fun areContentsTheSame(oldItem: Message, newItem: Message): Boolean {
        return oldItem == newItem
    }
}


