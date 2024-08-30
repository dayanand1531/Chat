package com.example.chat.api.request

data class AccessTokenRequest(
    val op: String,
    val token: String,
    val user: User
)