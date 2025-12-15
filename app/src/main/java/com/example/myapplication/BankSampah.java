package com.example.myapplication;

import com.google.android.gms.maps.model.LatLng;

public class BankSampah {
    private String nama;
    private String alamat;
    private LatLng lokasi;
    private float jarak; // Menyimpan jarak dalam meter

    public BankSampah(String nama, String alamat, LatLng lokasi) {
        this.nama = nama;
        this.alamat = alamat;
        this.lokasi = lokasi;
        this.jarak = 0; // Default 0
    }

    public String getNama() {
        return nama;
    }

    public String getAlamat() {
        return alamat;
    }

    public LatLng getLokasi() {
        return lokasi;
    }

    public float getJarak() {
        return jarak;
    }

    public void setJarak(float jarak) {
        this.jarak = jarak;
    }
}
