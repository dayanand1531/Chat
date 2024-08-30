package com.example.chat.room.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.chat.api.model.entity.Message

@Dao
interface MessageDao {

    @Query("update Message set message = :message,Time = :timestamp, messageType = :messageType , imageUrl = :filePath,latitude = :latitude,longitude = :longitude,messageStatus =:status,peer =:peer where mid=:mid")
    fun updateMessage(
        message: String?,
        timestamp: Long,
        messageType: Int,
        filePath: String?,
        latitude: Double,
        longitude: Double,
        status: Int,
        peer: String,
        mid: Long
    )

    @Query("select exists(select * from Message where mid=:mid)")
    fun isMessageExist(mid: Long): Boolean

    @Insert
    fun saveMessage(message: Message)

    @Query("select * from Message where (senderId = :receiverId and ReceiverId=:senderId) or (ReceiverId = :receiverId and senderId=:senderId ) ")
    fun getMessageList(receiverId: Long, senderId: Long): LiveData<List<Message>>
}