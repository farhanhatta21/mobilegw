package com.example.pblmobile.Models

data class BeritaResponse(
    val status: Boolean,
    val message: String,
    val berita: List<BeritaItem>,
    val gambar_path: String

)