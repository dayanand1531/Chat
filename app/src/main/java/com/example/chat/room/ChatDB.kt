package com.example.chat.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.chat.api.model.Contact
import com.example.chat.api.model.entity.Message
import com.example.chat.api.model.entity.User
import com.example.chat.room.dao.ContactDao
import com.example.chat.room.dao.MessageDao
import com.example.chat.room.dao.UserDao

@Database(entities = [Contact::class,User::class,Message::class], version = 1, exportSchema = true)
abstract class ChatDB: RoomDatabase() {

    abstract fun contactDao():ContactDao
    abstract fun userDao():UserDao
    abstract fun messageDao():MessageDao
}