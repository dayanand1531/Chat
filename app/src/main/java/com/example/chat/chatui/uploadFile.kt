package com.example.chat.chatui

import com.mesibo.api.Mesibo
import com.mesibo.api.MesiboFileTransfer

class FileUpload(filePath: String, recipientId: String):Mesibo.FileTransferHandler {


    override fun Mesibo_onStartFileTransfer(p0: MesiboFileTransfer?): Boolean {
    p0?.upload
        return true
    }

    override fun Mesibo_onStopFileTransfer(p0: MesiboFileTransfer?): Boolean {
       return true
    }

}