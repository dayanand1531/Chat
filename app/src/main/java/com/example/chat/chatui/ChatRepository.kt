package com.example.chat.chatui

import android.util.Log
import com.example.chat.api.model.entity.Message
import com.example.chat.api.model.entity.User
import com.example.chat.room.ChatDB
import com.mesibo.api.Mesibo
import com.mesibo.api.MesiboMessage
import com.mesibo.api.MesiboProfile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.util.Date
import javax.inject.Inject


class ChatRepository @Inject constructor(
    private val appDatabase: ChatDB
) {
    fun storeMessage(message: MesiboMessage, loggedInUser: Long, username: Long) {
        runBlocking {
            if (!message.isOutgoing) {
                val address = message.profile.address
                val isUserExist = isUserExists(address)
                if (!isUserExist) {
                    val (userProfile, userAddress) = findUserInMesibo(address)
                    val user = User(
                        id = 0,
                        name = userProfile.name,
                        UID = userProfile.uid.toString(),
                        status = null,
                        zipCode = null,
                        profileUrl = null,
                        address = null,
                        update = false,
                        user = userProfile.address
                    )
                    insertUser(user)
                    saveAndUpdateMessage(message, loggedInUser, username)
                } else saveAndUpdateMessage(message, loggedInUser, username)
            } else saveAndUpdateMessage(message, username, loggedInUser)
        }
    }

    suspend fun insertUser(user: User) = withContext(Dispatchers.IO) {
        appDatabase.userDao().saveUser(user)
    }

    suspend fun isUserExists(number: String) = withContext(Dispatchers.IO) {
        appDatabase.userDao().isUserExist(number)
    }

    suspend fun findUserInMesibo(number: String): Pair<MesiboProfile, String> =
        withContext(Dispatchers.IO) {
            val userProfile = Mesibo.getProfile(number)
            val username = userProfile.name
            Log.d("TAG", "findUserInMesibo: ${username}")
            Pair(userProfile, username)
        }

    fun saveAndUpdateMessage(message: MesiboMessage, loggedInUser: Long, username: Long) {
        GlobalScope.launch(Dispatchers.IO) {
            //  val timestamp = convertDateToLong(message.timestamp)
            val messageItem = Message(
                0,
                mid = message.messageId,
                message = message.message,
                timestamp = message.ts,
                isSent = message.isOutgoing,
                messageType = message.getInt("type", 0),
                imageUrl = message.file?.url,
                latitude = message.latitude,
                longitude = message.longitude,
                messageStatus = message.status,
                senderId = loggedInUser,
                receiverId = username,
                peer = message.peer
            )
            val isMessageExist = appDatabase.messageDao().isMessageExist(message.messageId)
            if (isMessageExist) appDatabase.messageDao().updateMessage(
                message = message.message,
                timestamp = message.ts,
                messageType = message.getInt("type", 0),
                filePath = message.file?.url,
                latitude = message.latitude,
                longitude = message.longitude,
                status = message.status,
                peer = message.peer,
                mid = message.messageId
            )
            else appDatabase.messageDao().saveMessage(messageItem)
        }
    }

    private fun convertDateToLong(date: Date): Long {
        val date = Date()  // Replace with your Date object
        val timeInMillis: Long = date.time
        return timeInMillis
    }

    suspend fun getMessage(senderId: Long, receiverId: Long) = withContext(Dispatchers.IO) {
        appDatabase.messageDao().getMessageList(receiverId,senderId)
    }


    /*  fun fetchAllMessages(userId: String): LiveData<List<com.example.chat.api.model.Message>> {
          val messagesLiveData = MutableLiveData<List<com.example.chat.api.model.Message>>()
          val messages = mutableListOf<com.example.chat.api.model.Message>()

          val readSession = MesiboReadSession(object : Mesibo.MessageListener {

              override fun Mesibo_onMessage(p0: MesiboMessage) {
                  p0.peer
              }

              override fun Mesibo_onMessageStatus(p0: MesiboMessage) {

              }

              override fun Mesibo_onMessageUpdate(p0: MesiboMessage) {

              }
          })
          readSession.enableReadReceipt(true)
          readSession.read(100)
          messagesLiveData.postValue(messages)
          return messagesLiveData
      }*/


}