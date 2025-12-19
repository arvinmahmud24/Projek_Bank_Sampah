package com.example.myapplication

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.firebase.database.*
import java.text.NumberFormat
import java.util.Locale

class KalkulatorActivity : AppCompatActivity() {

    private lateinit var tvNamaSampah: TextView
    private lateinit var tvHargaPerUnit: TextView
    private lateinit var tvTotalHarga: TextView
    private lateinit var etBerat: EditText
    private lateinit var btnHitung: Button
    private lateinit var btnSimpan: Button
    
    private var hargaSatuan: Int = 0
    private var totalPoinTerakhir: Int = 0
    private var userId: String? = null
    private lateinit var dbRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kalkulator)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        tvNamaSampah = findViewById(R.id.tvNamaSampah)
        tvHargaPerUnit = findViewById(R.id.tvHargaPerUnit)
        tvTotalHarga = findViewById(R.id.tvTotalHarga)
        etBerat = findViewById(R.id.etBerat)
        btnHitung = findViewById(R.id.btnHitung)
        btnSimpan = findViewById(R.id.btnSimpan)

        // Ambil data dari Intent
        userId = intent.getStringExtra("USER_ID")
        val namaSampah = intent.getStringExtra("NAMA_SAMPAH")
        val hargaSampahString = intent.getStringExtra("HARGA_SAMPAH")

        if (hargaSampahString != null) {
            val hargaAngka = hargaSampahString.replace(Regex("[^0-9]"), "")
            hargaSatuan = hargaAngka.toIntOrNull() ?: 0
        }

        tvNamaSampah.text = namaSampah
        tvHargaPerUnit.text = hargaSampahString

        btnHitung.setOnClickListener { hitungTotal() }
        
        btnSimpan.setOnClickListener { simpanKeSaldo() }
        
        dbRef = FirebaseDatabase.getInstance().reference
    }

    private fun hitungTotal() {
        val beratString = etBerat.text.toString()
        if (beratString.isEmpty()) {
            tvTotalHarga.text = "Rp. 0"
            btnSimpan.visibility = View.GONE
            return
        }

        try {
            val berat = beratString.toDouble()
            val total = (berat * hargaSatuan).toInt()
            totalPoinTerakhir = total

            val formatRupiah = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
            tvTotalHarga.text = formatRupiah.format(total)
            
            btnSimpan.visibility = View.VISIBLE
        } catch (e: NumberFormatException) {
            tvTotalHarga.text = "Input tidak valid"
            btnSimpan.visibility = View.GONE
        }
    }

    private fun simpanKeSaldo() {
        if (userId == null) {
            Toast.makeText(this, "Sesi user tidak ditemukan. Silakan login ulang.", Toast.LENGTH_SHORT).show()
            return
        }

        val userPoinRef = dbRef.child("users").child(userId!!).child("poin")

        // Gunakan Transaction agar penambahan poin aman
        userPoinRef.runTransaction(object : Transaction.Handler {
            override fun doTransaction(mutableData: MutableData): Transaction.Result {
                val currentPoin = mutableData.getValue(Int::class.java) ?: 0
                mutableData.value = currentPoin + totalPoinTerakhir
                return Transaction.success(mutableData)
            }

            override fun onComplete(error: DatabaseError?, committed: Boolean, snapshot: DataSnapshot?) {
                if (committed) {
                    catatKeRiwayat()
                    Toast.makeText(this@KalkulatorActivity, "Berhasil simpan ke saldo!", Toast.LENGTH_LONG).show()
                    finish() // Tutup halaman setelah simpan
                } else {
                    Toast.makeText(this@KalkulatorActivity, "Gagal simpan: ${error?.message}", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun catatKeRiwayat() {
        val riwayatRef = dbRef.child("riwayat_transaksi").push()
        val dataRiwayat = hashMapOf(
            "userId" to userId,
            "tanggal" to "Baru saja", // Anda bisa gunakan Date() yang diformat
            "deskripsi" to "Setor ${tvNamaSampah.text}",
            "poin" to "+${totalPoinTerakhir}",
            "isMasuk" to true
        )
        riwayatRef.setValue(dataRiwayat)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}
