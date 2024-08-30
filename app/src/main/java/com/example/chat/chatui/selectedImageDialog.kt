package com.example.chat.chatui

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.view.LayoutInflater
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.example.chat.R
import com.example.chat.util.MessageType
import com.mesibo.api.Mesibo
import com.mesibo.api.MesiboMessage
import java.io.InputStream

class Dialog {
    companion object {
        fun showImageDialog(imageUrl: Uri?, context: Context, username: String) {
            val dialogView =
                LayoutInflater.from(context).inflate(R.layout.dialog_selected_image_preview, null)
            val imageView: ImageView = dialogView.findViewById(R.id.ivSelectedImg)
            val btnCancel: Button = dialogView.findViewById(R.id.btnCancel)
            val btnSend: Button = dialogView.findViewById(R.id.btnSend)

            Glide.with(context)
                .load(imageUrl)
                .placeholder(R.drawable.ic_image_24) // Optional placeholder
                .error(R.drawable.ic_launcher_background) // Optional error image
                .into(imageView)

            // Build and show the AlertDialog
            val dialog = AlertDialog.Builder(context)
                .setView(dialogView)
                .create()
            dialog.show()
            btnSend.setOnClickListener { sendMsg(username,imageUrl,dialog,context) }
            btnCancel.setOnClickListener { dialog.dismiss() }
        }

        private fun sendMsg(username: String, imageUrl: Uri?, dialog: AlertDialog, context: Context) {
            val profile = Mesibo.getProfile(username)
            val mesibo: MesiboMessage = profile.newMessage()
            val bitmapImage = getBitmapFromUri(context = context, imageUrl!!)
            mesibo.setContent(bitmapImage)
            mesibo.setInt("type",MessageType.IMAGE.value)
            mesibo.setString("localPath",imageUrl.toString())
            mesibo.send()
            if (dialog.isShowing) dialog.dismiss()
        }

        private fun getBitmapFromUri(context: Context, uri: Uri): Bitmap? {
            return try {
                val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
                BitmapFactory.decodeStream(inputStream)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }
}