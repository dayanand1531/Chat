package com.example.chat.contact

import android.content.ContentResolver
import android.provider.ContactsContract
import android.util.Log
import com.example.chat.ChatApplication
import com.example.chat.api.model.Contact
import com.example.chat.api.model.entity.User
import com.example.chat.room.ChatDB
import com.mesibo.api.Mesibo
import com.mesibo.api.MesiboProfile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ContactRepository @Inject constructor(
    private val appData: ChatDB,
    private val contentResolver: ContentResolver
) {

    suspend fun clearDB() = withContext(Dispatchers.IO) {
        appData.clearAllTables()
    }

    suspend fun fetch() {
        if (Mesibo.getInstance() == null) {
           ChatApplication().initializeMesibo()
            Log.d("TAG", "Mesibo initialized")
        }
        val contactList = getContacts()

        contactList.forEach {
            val (userProfile, username) = findUserInMesibo(it.number)
            Log.d("TAG", "fetch: $username ${it.number}")
            if (username.isNotEmpty()) {
                val user = User(
                    id = 0,
                    name = userProfile.name,
                    UID = userProfile.uid.toString(),
                    status = null,
                    zipCode = null,
                    profileUrl = null,
                    address = null,
                    update = false,
                    user = userProfile.address
                )
                val isUserExists = isUserExists(it.number)
                if (isUserExists) {
                    val id = updateUser(user)
                    Log.d("TAG", "update: $id")
                } else insertUser(user)
            } else {
                val isExists = isContact(it)
                if (isExists) updateContact(it)
                else insertContact(it)
            }
        }
    }

    private suspend fun insertContact(it: Contact) = withContext(Dispatchers.IO) {
        appData.contactDao().saveContact(it)
    }

    private suspend fun updateContact(it: Contact) = withContext(Dispatchers.IO) {
        appData.contactDao().updateContact(it.name, it.number)
    }

    private suspend fun isContact(it: Contact) = withContext(Dispatchers.IO) {
        val isExists = appData.contactDao().isContactExist(it.number)
        isExists
    }

     suspend fun insertUser(user: User) = withContext(Dispatchers.IO) {
        appData.userDao().saveUser(user)
    }

    private suspend fun updateUser(user: User) = withContext(Dispatchers.IO) {
        val id = appData.userDao().updateUser(
            user.name,
            user.UID,
            user.status,
            user.zipCode,
            user.profileUrl,
            user.address,
            user.update,
            user.user,
        )
        id
    }

    suspend fun isUserExists(number: String) = withContext(Dispatchers.IO) {
      appData.userDao().isUserExist(number)
    }

     suspend fun findUserInMesibo(number: String): Pair<MesiboProfile, String> = withContext(Dispatchers.IO) {
        val userProfile = Mesibo.getProfile(number)
        val username = userProfile.name
        Log.d("TAG", "findUserInMesibo: ${username}")
        Pair(userProfile, username)
    }

    suspend fun getContactList() = withContext(Dispatchers.IO) {
        appData.contactDao().getContact()
    }

    suspend fun getUserList() = withContext(Dispatchers.IO) {
        appData.userDao().getUserList()
    }

    private suspend fun getContacts() = withContext(Dispatchers.IO) {
        val contactsList = mutableListOf<Contact>()

        val cursor = contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            arrayOf(
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER
            ),
            null,
            null,
            null
        )

        cursor?.use {
            val nameIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
            val numberIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)

            while (it.moveToNext()) {
                val name = it.getString(nameIndex) ?: ""
                val number = it.getString(numberIndex)
                    .replace(" ", "")
                    .replace("-", "")

                val contactInfo = if (name.isNotBlank()) {
                    name
                } else {
                    number
                }

                contactsList.add(Contact(0, contactInfo, number))
            }
        }

        contactsList.distinct()
    }
}