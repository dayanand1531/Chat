package com.example.chat.contact

import com.mesibo.api.Mesibo
import com.mesibo.api.MesiboReadSession

class MessageSync:Mesibo.SyncListener {
    override fun Mesibo_onSync(sync: MesiboReadSession?, p1: Int) {
             sync?.sentMessageCount
        sync?.totalMessageCount
        sync?.unreadMessageCount
        if(p1 <= 0) return
        sync?.read(p1)
    }
}