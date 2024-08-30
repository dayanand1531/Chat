package com.example.chat.api.response

data class AccessTokenResponse(
    val op: String,
    val result: Boolean,
    val user: User
)