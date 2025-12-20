package com.example.myapplication

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*

class SetorSampahActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: SampahAdapter
    private val sampahList = mutableListOf<Sampah>()
    private lateinit var btnSetorkan: Button
    private lateinit var tvHeader: TextView
    
    private var currentGrandTotal: Double = 0.0
    private var userId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setor_sampah)

        val sharedPref = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        userId = sharedPref.getString("USER_ID", null)

        tvHeader = findViewById(R.id.tv_nama_bank_header)
        btnSetorkan = findViewById(R.id.btn_proses_setor)
        recyclerView = findViewById(R.id.rvSetorSampah)
        
        val namaBank = intent.getStringExtra("NAMA_BANK") ?: "Bank Sampah"
        tvHeader.text = "Setor ke: $namaBank"

        recyclerView.layoutManager = LinearLayoutManager(this)
        populateSampahList()

        adapter = SampahAdapter(sampahList, isReadOnly = false) { total ->
            currentGrandTotal = total
            if (total > 0) {
                btnSetorkan.text = "Setorkan (pts ${String.format("%,.0f", total)})"
            } else {
                btnSetorkan.text = "Setorkan Sekarang"
            }
        }
        recyclerView.adapter = adapter

        btnSetorkan.setOnClickListener {
            if (userId == null) {
                Toast.makeText(this, "Sesi berakhir, silakan login ulang", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (currentGrandTotal > 0) {
                showConfirmationDialog(namaBank)
            } else {
                Toast.makeText(this, "Harap masukkan jumlah sampah", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showConfirmationDialog(namaBank: String) {
        AlertDialog.Builder(this)
            .setTitle("Konfirmasi Setoran")
            .setMessage("Anda akan menyetor sampah senilai ${currentGrandTotal.toInt()} poin ke $namaBank. Lanjutkan?")
            .setPositiveButton("Ya") { _, _ -> simpanKeFirebase(namaBank) }
            .setNegativeButton("Tidak", null)
            .show()
    }

    private fun simpanKeFirebase(namaBank: String) {
        val database = FirebaseDatabase.getInstance().reference
        val userRef = database.child("users").child(userId!!)
        
        userRef.child("poin").runTransaction(object : Transaction.Handler {
            override fun doTransaction(mutableData: MutableData): Transaction.Result {
                val current = mutableData.getValue(Int::class.java) ?: 0
                mutableData.value = current + currentGrandTotal.toInt()
                return Transaction.success(mutableData)
            }

            override fun onComplete(error: DatabaseError?, committed: Boolean, snapshot: DataSnapshot?) {
                if (committed) {
                    catatRiwayat(namaBank)
                    Toast.makeText(this@SetorSampahActivity, "Setoran Berhasil!", Toast.LENGTH_LONG).show()
                    
                    // Navigasi ke halaman riwayat (tab Mutasi di MainActivity)
                    val intent = Intent(this@SetorSampahActivity, MainActivity::class.java).apply {
                        putExtra("OPEN_TAB", "MUTASI")
                        flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this@SetorSampahActivity, "Gagal memproses poin: ${error?.message}", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun catatRiwayat(namaBank: String) {
        val riwayatRef = FirebaseDatabase.getInstance().getReference("riwayat_transaksi").push()
        val tanggal = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()).format(Date())
        val setoranData = adapter.getSetoranData()
        val deskripsiBuilder = StringBuilder("Setor di $namaBank: ")
        setoranData.forEach { (pos, berat) -> if (berat > 0) deskripsiBuilder.append("${sampahList[pos].nama} ($berat Kg), ") }
        
        val dataRiwayat = hashMapOf(
            "userId" to userId,
            "tanggal" to tanggal,
            "deskripsi" to deskripsiBuilder.toString().removeSuffix(", "),
            "poin" to "+${currentGrandTotal.toInt()}",
            "isMasuk" to true
        )
        riwayatRef.setValue(dataRiwayat)
    }

    private fun populateSampahList() {
        sampahList.add(Sampah("Minyak Jelantah", "Rp. 3.600"))
        sampahList.add(Sampah("Ember Warna", "Rp. 1.500"))
        sampahList.add(Sampah("Besi A", "Rp. 2.760"))
        sampahList.add(Sampah("Kardus", "Rp. 1.260"))
        sampahList.add(Sampah("Tembaga", "Rp. 45.000"))
        sampahList.add(Sampah("Aki Bekas", "Rp. 6.000"))
        sampahList.add(Sampah("Kantong Kresek", "Rp. 50"))
        sampahList.add(Sampah("Beling Putih", "Rp. 120"))
    }
}
