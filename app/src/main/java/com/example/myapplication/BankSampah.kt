package com.example.myapplication

import com.google.android.gms.maps.model.LatLng
import com.google.firebase.database.Exclude

/**
 * Data class untuk merepresentasikan Bank Sampah.
 * Dibuat agar kompatibel dengan Firebase Realtime Database.
 */
data class BankSampah(
    // Properti yang langsung dipetakan ke field di Firebase
    var nama: String = "",
    var alamat: String = "",
    var latitude: Double = 0.0,
    var longitude: Double = 0.0,

    // Properti yang dikelola secara lokal di aplikasi dan tidak disimpan di Firebase
    @get:Exclude
    var id: String = "",
    @get:Exclude
    var jarak: Float = 0f
) {
    /**
     * Fungsi bantuan untuk mendapatkan objek LatLng dari data latitude dan longitude.
     * Anotasi @get:Exclude mencegah Firebase mencoba memetakan fungsi ini.
     */
    fun getLokasi(): LatLng {
        return LatLng(latitude, longitude)
    }
}
