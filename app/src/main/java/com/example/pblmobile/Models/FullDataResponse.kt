package com.example.pblmobile.Models

data class FullDataResponse(
    val status: Boolean,
    val message: String,
    val data: List<AdminResponse>
)
