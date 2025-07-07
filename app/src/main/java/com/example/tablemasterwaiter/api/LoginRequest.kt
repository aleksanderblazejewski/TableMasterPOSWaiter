package com.example.tablemasterwaiter.api

data class LoginRequest(
    val username: String,
    val password_hash: String
)