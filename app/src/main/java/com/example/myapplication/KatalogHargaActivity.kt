package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class KatalogHargaActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: SampahAdapter
    private lateinit var sampahList: MutableList<Sampah>
    private lateinit var tvNamaBankSampah: TextView
    private lateinit var btnSetorSampah: Button
    
    private var currentGrandTotal: Double = 0.0
    private var username: String? = null
    private var email: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_katalog_harga)

        // Tangkap data user agar tidak hilang saat kembali ke Home
        username = intent.getStringExtra("USERNAME")
        email = intent.getStringExtra("EMAIL")

        tvNamaBankSampah = findViewById(R.id.tv_nama_bank_sampah)
        btnSetorSampah = findViewById(R.id.btn_setor_sampah)
        recyclerView = findViewById(R.id.recyclerViewSampah)
        
        val namaBank = intent.getStringExtra("NAMA_BANK_SAMPAH") ?: "Bank Sampah"
        tvNamaBankSampah.text = namaBank

        recyclerView.layoutManager = LinearLayoutManager(this)
        sampahList = mutableListOf()
        populateSampahList()

        // Inisialisasi adapter dengan callback perubahan total harga
        adapter = SampahAdapter(sampahList) { total ->
            currentGrandTotal = total
            if (total > 0) {
                btnSetorSampah.text = "Setor Sampah (Rp ${String.format("%,.0f", total)})"
            } else {
                btnSetorSampah.text = "Setor Sampah"
            }
        }
        recyclerView.adapter = adapter

        btnSetorSampah.setOnClickListener {
            if (currentGrandTotal > 0) {
                showConfirmationDialog(namaBank)
            } else {
                Toast.makeText(this, "Silakan masukkan berat sampah terlebih dahulu", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showConfirmationDialog(namaBank: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Konfirmasi Setor Sampah")
        builder.setMessage("Apakah Anda yakin ingin menyetor sampah ke $namaBank dengan total Rp ${String.format("%,.0f", currentGrandTotal)}?")
        
        builder.setPositiveButton("Ya") { _, _ ->
            simpanTransaksiKeFirebase(namaBank)
        }
        
        builder.setNegativeButton("Tidak") { dialog, _ ->
            dialog.dismiss()
        }
        
        builder.show()
    }

    private fun simpanTransaksiKeFirebase(namaBank: String) {
        val dbRef = FirebaseDatabase.getInstance().getReference("transaksi")
        
        val setoranData = adapter.getSetoranData()
        val deskripsiBuilder = StringBuilder("Setor di $namaBank: ")
        
        setoranData.forEach { (position, berat) ->
            if (berat > 0) {
                deskripsiBuilder.append("${sampahList[position].nama} ($berat Kg), ")
            }
        }
        
        val deskripsi = deskripsiBuilder.toString().removeSuffix(", ")
        val tanggal = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()).format(Date())
        val totalHarga = "Rp ${String.format("%,.0f", currentGrandTotal)}"

        val transaksiBaru = Transaksi(
            tanggal = tanggal,
            deskripsi = deskripsi,
            poin = totalHarga,
            isMasuk = true
        )

        dbRef.push().setValue(transaksiBaru).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "Transaksi Berhasil Disimpan!", Toast.LENGTH_LONG).show()
                
                // Kembali ke Home dengan membawa kembali data USERNAME & EMAIL
                val intent = Intent(this, MainActivity::class.java).apply {
                    putExtra("USERNAME", username)
                    putExtra("EMAIL", email)
                    flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                }
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Gagal menyimpan transaksi: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun populateSampahList() {
        sampahList.add(Sampah("Minyak Jelantah (Liter)", "Rp. 5,000"))
        sampahList.add(Sampah("Emberan warna (Kg)", "Rp. 960"))
        sampahList.add(Sampah("Kertas putihan (Kg)", "Rp. 1,120"))
        sampahList.add(Sampah("Kardus (Kg)", "Rp. 1,200"))
        sampahList.add(Sampah("Duplex (Kg)", "Rp. 480"))
        sampahList.add(Sampah("Alumunium rongsok (Kg)", "Rp. 6,400"))
        sampahList.add(Sampah("Kaleng (Kg)", "Rp. 1,440"))
        sampahList.add(Sampah("Besi A (Kg)", "Rp. 2,400"))
        sampahList.add(Sampah("Koran (Kg)", "Rp. 800"))
        sampahList.add(Sampah("Botol Beling (Kg)", "Rp. 160"))
    }
}