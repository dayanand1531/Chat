package com.example.chat.login

import com.example.chat.api.ApiService
import com.example.chat.api.request.AccessTokenRequest
import javax.inject.Inject

class LoginRepository @Inject constructor(private val apiService: ApiService){
    suspend fun getAccessToken(accessTokenRequest: AccessTokenRequest) = apiService.getAccessToken(accessTokenRequest)


}