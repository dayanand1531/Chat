package com.example.chat.api

import com.example.chat.api.request.AccessTokenRequest
import com.example.chat.api.response.AccessTokenResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {
    @POST("backend")
    suspend fun getAccessToken(
        @Body accessTokenRequest: AccessTokenRequest
    ): Response<AccessTokenResponse>
}