package com.example.myapplication

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.databinding.ActivityKalkulatorHargaBinding
import java.text.NumberFormat
import java.util.Locale

class KalkulatorHargaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityKalkulatorHargaBinding
    private var hargaSatuan = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityKalkulatorHargaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Ambil data dari Intent
        val namaSampah = intent.getStringExtra("NAMA_SAMPAH")
        val hargaSampahString = intent.getStringExtra("HARGA_SAMPAH") // e.g., "Rp 5.000"

        // Parse harga dari string (menghilangkan non-digit)
        hargaSampahString?.let {
            val hargaAngka = it.replace(Regex("[^0-9]"), "")
            hargaSatuan = hargaAngka.toIntOrNull() ?: 0
        }

        // Tampilkan info
        binding.tvNamaSampah.text = namaSampah
        binding.tvHargaPerUnit.text = hargaSampahString

        // Listener untuk tombol hitung
        binding.btnHitung.setOnClickListener { hitungTotal() }
    }

    private fun hitungTotal() {
        val beratString = binding.etBerat.text.toString()
        if (beratString.isEmpty()) {
            binding.tvTotalHarga.text = formatRupiah(0.0)
            return
        }

        val berat = beratString.toDoubleOrNull()
        if (berat == null) {
            binding.tvTotalHarga.text = "Input tidak valid"
            return
        }

        val total = berat * hargaSatuan
        binding.tvTotalHarga.text = formatRupiah(total)
    }

    private fun formatRupiah(nilai: Double): String {
        val formatRupiah = NumberFormat.getCurrencyInstance(Locale("in", "ID"))
        return formatRupiah.format(nilai)
    }
}
