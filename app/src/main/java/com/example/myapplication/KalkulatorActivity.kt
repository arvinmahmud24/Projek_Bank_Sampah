package com.example.myapplication

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import java.text.NumberFormat
import java.util.Locale

class KalkulatorActivity : AppCompatActivity() {

    private lateinit var tvNamaSampah: TextView
    private lateinit var tvHargaPerUnit: TextView
    private lateinit var tvTotalHarga: TextView
    private lateinit var etBerat: EditText
    private lateinit var btnHitung: Button
    private var hargaSatuan: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kalkulator)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true) // Menampilkan tombol kembali

        tvNamaSampah = findViewById(R.id.tvNamaSampah)
        tvHargaPerUnit = findViewById(R.id.tvHargaPerUnit)
        tvTotalHarga = findViewById(R.id.tvTotalHarga)
        etBerat = findViewById(R.id.etBerat)
        btnHitung = findViewById(R.id.btnHitung)

        // Ambil data dari Intent
        val namaSampah = intent.getStringExtra("NAMA_SAMPAH")
        val hargaSampahString = intent.getStringExtra("HARGA_SAMPAH")

        // Parse harga dari string
        if (hargaSampahString != null) {
            val hargaAngka = hargaSampahString.replace(Regex("[^0-9]"), "")
            hargaSatuan = hargaAngka.toIntOrNull() ?: 0
        }

        // Tampilkan info
        tvNamaSampah.text = namaSampah
        tvHargaPerUnit.text = hargaSampahString

        // Listener untuk tombol hitung
        btnHitung.setOnClickListener { hitungTotal() }
    }

    private fun hitungTotal() {
        val beratString = etBerat.text.toString()
        if (beratString.isEmpty()) {
            tvTotalHarga.text = "Rp. 0"
            return
        }

        try {
            val berat = beratString.toDouble()
            val total = berat * hargaSatuan
            val formatRupiah = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
            tvTotalHarga.text = formatRupiah.format(total)
        } catch (e: NumberFormatException) {
            tvTotalHarga.text = "Input tidak valid"
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed() // Fungsi untuk tombol kembali
        return true
    }
}
