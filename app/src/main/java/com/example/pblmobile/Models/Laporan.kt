package com.example.pblmobile.Models

data class Laporan(
    val id_lapor: Int,
    val nama: String,
    val lokasi: String,
    val bukti: String,
    val id_akun: Int,
    val stats: String,
    val latitude: Double, // Sudah mendukung latitude
    val longitude: Double, // Sudah mendukung longitude
    val tanggal: String
)
