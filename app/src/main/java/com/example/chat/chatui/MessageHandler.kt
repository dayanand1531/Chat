package com.example.chat.chatui

import android.util.Log
import com.example.chat.room.ChatDB
import com.mesibo.api.Mesibo
import com.mesibo.api.MesiboMessage
import javax.inject.Inject

class MessageHandler :Mesibo.MessageListener,Mesibo.ConnectionListener {
    /*@Inject
    lateinit var chatRepository: ChatRepository*/

    override fun Mesibo_onMessage(message: MesiboMessage) {
     // chatRepository.saveMessae(message)
        Log.d("Mesibo", "Message sync: ${message.message}, From: ${message.senderProfile}, To: ${message.messageId}")
    }

    override fun Mesibo_onMessageStatus(p0: MesiboMessage) {

    }

    override fun Mesibo_onMessageUpdate(p0: MesiboMessage) {

    }

    override fun Mesibo_onConnectionStatus(p0: Int) {

    }
}