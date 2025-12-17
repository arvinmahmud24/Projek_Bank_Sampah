package com.example.myapplication;

public class Transaksi {
    private String tanggal;
    private String deskripsi;
    private String poin; // e.g. "+500" atau "-200"
    private boolean isMasuk; // true = pemasukan, false = pengeluaran

    public Transaksi(String tanggal, String deskripsi, String poin, boolean isMasuk) {
        this.tanggal = tanggal;
        this.deskripsi = deskripsi;
        this.poin = poin;
        this.isMasuk = isMasuk;
    }

    public String getTanggal() {
        return tanggal;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public String getPoin() {
        return poin;
    }

    public boolean isMasuk() {
        return isMasuk;
    }
}
