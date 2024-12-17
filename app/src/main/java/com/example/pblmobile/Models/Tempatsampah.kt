package com.example.pblmobile.Models

data class Tempatsampah(
    val id_tempatsampah: String,  // Unique ID of the trash bin
    val longtitude: String,  // Longitude of the trash bin
    val latitude: String,  // Latitude of the trash bin
    val status: String  // Status of the trash bin (kosong, terisi, penuh)
)