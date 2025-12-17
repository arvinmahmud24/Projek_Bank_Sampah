package com.example.myapplication

import com.google.android.gms.maps.model.LatLng

// Pastikan definisi data class ini benar
data class BankSampah(
    val nama: String,
    val alamat: String,
    val lokasi: LatLng, // Tipe data harus LatLng
    var jarak: Float = 0f
)