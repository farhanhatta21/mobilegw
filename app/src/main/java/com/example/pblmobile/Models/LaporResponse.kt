package com.example.pblmobile.Models

data class LaporResponse(
    val status: Boolean,
    val message: String,
    val laporan: List<Laporan>
)
