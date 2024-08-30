package com.example.chat.room.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import androidx.room.Upsert
import com.example.chat.api.model.Contact

@Dao
interface ContactDao {

    @Insert
     suspend fun saveContact(contact: Contact)

    @Query("update contact set name=:name, phoneNumber=:phone where phoneNumber=:phone")
    fun updateContact(name:String,phone:String)

    @Query("select Exists(select * from contact where phoneNumber=:phone)")
    fun isContactExist(phone:String):Boolean


    @Transaction
    @Query("SELECT * FROM contact")
     fun getContact():LiveData<List<Contact>>

}