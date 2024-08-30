package com.example.chat.util

import android.content.Context

class ChatPerference(context: Context) {
    val sharedPreferences = context.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)

    fun storeAcceesToken(acceesToken: String): Boolean {
        val editor = sharedPreferences.edit()
        editor.putString("acceesToken", acceesToken)
        return editor.commit()
    }

    fun getacceesToken(): String? {
        return sharedPreferences.getString("acceesToken", null)
    }

    fun storeUId(uId: Long): Boolean {
        val editor = sharedPreferences.edit()
        editor.putLong("uid", uId)
        return editor.commit()
    }

    fun authId(): Long {
        return sharedPreferences.getLong("uid", 0)
    }

    fun clear(): Boolean {
        val editor = sharedPreferences.edit()
        editor.clear()
        return editor.commit()
    }

}