package com.example.pblmobile.Models

data class TempatsampahResponse(
    val status: Boolean,
    val message: String,
    val tempat_sampah: List<Tempatsampah>
)
