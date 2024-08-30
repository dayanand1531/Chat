package com.example.chat.api.model.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import okhttp3.Address

@Entity(tableName = "User")
data class User(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo("ID")
    val id:Int=0,
    @ColumnInfo("Name")
    val name:String,
    @ColumnInfo("Uid")
    val UID:String,
    @ColumnInfo("Status")
    val status:String?,
    @ColumnInfo("ZipCode")
    val zipCode:String?,
    @ColumnInfo("ProfileUrl")
    val profileUrl:String?,
    @ColumnInfo("Address")
    val address: String?,
    @ColumnInfo("IsUpdate")
    val update:Boolean=false,
    @ColumnInfo("username")
    val user: String
)
