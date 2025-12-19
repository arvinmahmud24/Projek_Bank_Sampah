package com.example.myapplication.ui

data class User(
    val id: String? = null,
    val username: String? = null,
    val email: String? = null,
    val password: String? = null,
    val poin: Int = 0 // Tambahkan field poin dengan default 0
)
