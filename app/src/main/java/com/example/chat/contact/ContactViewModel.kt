package com.example.chat.contact

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import com.example.chat.api.model.Contact
import com.example.chat.api.model.entity.User
import com.example.chat.chatui.ChatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ContactViewModel @Inject constructor(
    private val contactRepository: ContactRepository
): ViewModel() {
    private val _contacts = MutableLiveData<List<Contact>>()
    val contacts: LiveData<List<Contact>> get() = _contacts

    private var _contactList = MutableStateFlow<List<Contact>?>(emptyList())
    val contactList: StateFlow<List<Contact>?> = _contactList

    private var _userList = MutableStateFlow<List<User>?>(emptyList())
    val userList: StateFlow<List<User>?> = _userList



    fun fetchContacts() {
        viewModelScope.launch {
            contactRepository.fetch()
        }
    }

     fun contactList(){
        viewModelScope.launch {
             contactRepository.getContactList().asFlow().collect{
                 _contactList.value = it.sortedBy { it.name }
            }
        }
    }

    fun userList(){
        viewModelScope.launch {
             contactRepository.getUserList().asFlow().collect{
                _userList.value = it.sortedBy { it.name }
            }
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