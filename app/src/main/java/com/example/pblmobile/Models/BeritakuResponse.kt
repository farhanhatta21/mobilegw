package com.example.pblmobile.Models

data class BeritakuResponse(
    val status: Boolean,
    val message: String,
    val berita: List<Beritaku>?,
    val gambar_path: String
)
