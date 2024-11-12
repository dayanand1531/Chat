package com.example.chat.chatui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import com.example.chat.api.model.entity.Message
import com.example.chat.contact.ContactRepository
import com.example.chat.util.MessageType
import com.mesibo.api.Mesibo
import com.mesibo.api.MesiboMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(private val chatRepository: ChatRepository,
  private val contactRepository: ContactRepository
): ViewModel() {
    private val _messageList = MutableStateFlow(emptyList<Message>())
    val messageList : StateFlow<List<Message>> = _messageList

//  fun storeMessage(message: MesiboMessage, loggedInUser: Long, username: String) {
//    viewModelScope.launch {
//      if (!message.isOutgoing) {
//       val address = message.profile.address
//        val isUserExist = contactRepository.isUserExists(address)
//        if (!isUserExist) {
//           contactRepository.findUserInMesibo(address)
//            chatRepository.saveAndUpdateMessage(message,loggedInUser)
//        }
//      }else chatRepository.saveAndUpdateMessage(message,loggedInUser)
//    }
//  }

    fun sendMsg(username: String,message: String) {
        val profile = Mesibo.getProfile(username)
        val mesibo: MesiboMessage = profile.newMessage()
        mesibo.message = message
        mesibo.setInt("type", MessageType.TEXT.value)
        mesibo.send()
    }

     fun sendLocation(lat: Double?, lng: Double?,username:String) {
        viewModelScope.launch(Dispatchers.IO) {
            val profile = Mesibo.getProfile(username)
            val mesiboMessage = profile.newMessage()
            if (lat != null) {
                mesiboMessage.latitude = lat
            }
            if (lng != null) {
                mesiboMessage.longitude = lng
            }
            mesiboMessage.setInt("type", MessageType.LOCATION.value)
            mesiboMessage.send()
        }
    }

     fun getMessageList(senderId:Long,receiverId:Long) {
         viewModelScope.launch {
              chatRepository.getMessage(senderId,receiverId).asFlow().collect{
                  _messageList.value = it.distinctBy { it.mid }
             }
         }
     }

  suspend fun messageList(senderId:Long, receiverId:Long)=chatRepository.getMessage(senderId,receiverId)
}