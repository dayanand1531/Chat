package com.example.chat.login

import android.content.Context
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chat.api.request.AccessTokenRequest
import com.example.chat.api.request.Token
import com.example.chat.api.request.User
import com.example.chat.api.response.AccessTokenResponse
import com.example.chat.util.Response
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val loginRepository: LoginRepository) :
    ViewModel() {
    private val _mobileNo = mutableStateOf("")
    val mobileNo: State<String> = _mobileNo
    private val _password = mutableStateOf("")
    val password: State<String> = _password

    private var _loginResponse =
        MutableStateFlow<Response<AccessTokenResponse>>(Response.loading(false))
    val loginResponse: StateFlow<Response<AccessTokenResponse>> = _loginResponse

    fun onPhoneChange(phone: String) {
        _mobileNo.value = phone
    }

    fun onPassword(password: String) {
        _password.value = password
    }

    internal fun logIn() {
        _loginResponse.value = Response.loading(true)
        val auth = FirebaseAuth.getInstance()
        auth.signInWithEmailAndPassword("${mobileNo.value}@gmail.com", password.value)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // val user = auth.currentUser
                    getAcceesToken(mobileNo.value, com.example.chat.BuildConfig.appKey)
                } else {
                    _loginResponse.value = Response.error("${task.exception?.message}", false)
                }
            }
    }

    internal fun getAcceesToken(number: String, appToken: String) {
        _loginResponse.value = (Response.loading(true))
        viewModelScope.launch {
            val acceesTokenRequest = accessTokenRequest(number, appToken)
            val result = loginRepository.getAccessToken(acceesTokenRequest)
            if (result.isSuccessful) {
                if (result.body() != null) {
                    _loginResponse.value = (Response.success(data = result.body()!!, false))
                } else _loginResponse.value = (Response.error(result.message(), false))
            } else {
                _loginResponse.value = (Response.error(result.message(), false))
            }
        }
    }

    private fun accessTokenRequest(number: String, appToken: String): AccessTokenRequest {
        val token = Token(appid = "com.example.chat", expiry = 525600)
        val user = User(address = number, token)
        return AccessTokenRequest(
            op = "useradd",
            token = appToken,
            user = user
        )
    }
}