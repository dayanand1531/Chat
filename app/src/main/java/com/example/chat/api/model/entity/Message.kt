package com.example.chat.api.model.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Message")
data class Message(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo("MessageId")
    val messageId: Long=0,
    @ColumnInfo("MId")
    val mid: Long,
    @ColumnInfo("Message")
    val message: String?,
    @ColumnInfo("Time")
    val timestamp: Long,
    @ColumnInfo("isSent")
    val isSent: Boolean,
    @ColumnInfo("MessageType")
    val messageType: Int,
    @ColumnInfo("ImageUrl")
    val imageUrl:String?,
    @ColumnInfo("Latitude")
    val latitude:Double=0.0,
    @ColumnInfo("Longitude")
    val longitude:Double=0.0,
    @ColumnInfo("MessageStatus")
    val messageStatus:Int,
    @ColumnInfo("SenderId")
    val senderId:Long,
    @ColumnInfo("ReceiverId")
    val receiverId:Long,
    @ColumnInfo("Peer")
    val peer:String?,

)
