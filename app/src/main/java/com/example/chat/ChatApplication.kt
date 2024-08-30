package com.example.chat

import android.app.Application
import android.content.Context
import android.util.Log
import com.android.volley.BuildConfig
import com.example.chat.chatui.ChatActivity
import com.example.chat.chatui.MessageHandler
import com.example.chat.util.ChatPerference
import com.mesibo.api.Mesibo
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class ChatApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        val accessToken = getAccessToken(this)
        if (accessToken != null) {
            initializeMesibo()
            Log.d("TAG", "mesibo initialized")
        } else {
            Log.d("TAG", "mesibo not initialized")
        }
        if (BuildConfig.DEBUG) {
            // Plant a tree for Timber in debug mode
            Timber.plant(Timber.DebugTree())
        }
    }

    fun initializeMesibo() {
        val accessToken = getAccessToken(this)
        Mesibo.getInstance().init(this)
        Mesibo.setAppName("Chat")
         Mesibo.addListener(MessageHandler())
        Mesibo.addListener(ChatActivity())
        Mesibo.setDatabase("chat")
        Mesibo.setAccessToken(accessToken)
        Mesibo.start()
    }

    private fun getAccessToken(context: Context): String? {
        val sharedPref = ChatPerference(context)
        return sharedPref.getacceesToken()
    }

}
