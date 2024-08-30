package com.example.chat.room.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.chat.api.model.entity.User

@Dao
interface UserDao {

    @Insert
    fun saveUser(user: User)

    @Query("update User set name=:name,UID=:uid,Status=:status,ZipCode=:zipcode,ProfileUrl=:profileUrl,Address=:address,IsUpdate=:update where username=:id")
    fun updateUser(
        name:String,
        uid:String,
        status: String?,
        zipcode: String?,
        profileUrl: String?,
        address: String?,
        update:Boolean,
        id:String):Int

    @Query("select exists(select * from User where username=:username)")
    fun isUserExist(username:String):Boolean

    @Query("select * from User")
    fun getUserList():LiveData<List<User>>
}