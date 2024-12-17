package com.example.pblmobile.Models

data class AdminResponse(
    val id_lapor: Int,
    val nama: String,
    val lokasi: String,
    val bukti: String,
    val id_akun: Int,
    val stats: String,
    val tanggal: String
)
