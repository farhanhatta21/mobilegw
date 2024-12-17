package com.example.pblmobile.Models

import java.math.BigDecimal

data class User(
    val id_akun: Int,
    val nama: String,
    val email: String,
    val password: String,
    val point: BigDecimal,
    val role: String
)