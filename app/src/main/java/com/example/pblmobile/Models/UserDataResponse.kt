package com.example.pblmobile.Models

data class UserDataResponse(
    val status: Boolean,
    val message: String,
    val data: UserData?
)