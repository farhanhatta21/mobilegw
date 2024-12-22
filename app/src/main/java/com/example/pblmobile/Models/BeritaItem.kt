package com.example.pblmobile.Models

data class BeritaItem(
    val id_berita: String,
    val judul: String,
    val gambar: String,
    val deskripsi: String,
    val detail: String,
    val waktu: String = "" // Default kosong untuk waktu
)
