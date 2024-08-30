package com.example.chat.contact

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chat.api.model.Contact
import com.example.chat.chatui.ChatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ContactViewModel @Inject constructor(
    private val contactRepository: ContactRepository
): ViewModel() {
    private val _contacts = MutableLiveData<List<Contact>>()
    val contacts: LiveData<List<Contact>> get() = _contacts

    fun fetchContacts() {
        viewModelScope.launch {
            contactRepository.fetch()
        }
    }

    suspend fun getContactList()=contactRepository.getContactList()
    suspend fun getUserList()=contactRepository.getUserList()

    fun clearDb(){
        viewModelScope.launch {
            contactRepository.clearDB()
        }
    }

}