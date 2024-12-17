package com.example.pblmobile.Models

data class LoginResponse(
    val status: Boolean,
    val message: String,
    val token: String?,
    val user: User
)

