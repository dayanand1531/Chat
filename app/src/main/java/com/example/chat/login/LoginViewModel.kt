package com.example.chat.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chat.api.request.AccessTokenRequest
import com.example.chat.api.request.Token
import com.example.chat.api.request.User
import com.example.chat.api.response.AccessTokenResponse
import com.example.chat.util.Response
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val loginRepository: LoginRepository) :
    ViewModel() {
    private var _loginResponse =
        MutableLiveData<Response<AccessTokenResponse>>()
    val loginResponse: LiveData<Response<AccessTokenResponse>>
        get() = _loginResponse

    internal fun getAcceesToken(number: String, appToken: String) {
        _loginResponse.postValue(Response.loading())
        viewModelScope.launch {
            val acceesTokenRequest = accessTokenRequest(number, appToken)
            val result = loginRepository.getAccessToken(acceesTokenRequest)
            if (result.isSuccessful) {
                if (result.body() != null) {
                    _loginResponse.postValue(Response.success(data = result.body()!!))
                } else _loginResponse.postValue(Response.error(result.message()))
            } else {
                _loginResponse.postValue(Response.error(result.message()))
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