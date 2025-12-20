package com.example.myapplication

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*

class TarikSaldoActivity : AppCompatActivity() {

    private lateinit var tvSaldoTersedia: TextView
    private lateinit var etNominal: EditText
    private lateinit var etTujuan: EditText
    private lateinit var btnKonfirmasi: Button
    
    private var userId: String? = null
    private var currentPoin: Int = 0
    private lateinit var dbRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tarik_saldo)

        val toolbar: Toolbar = findViewById(R.id.toolbarTarik)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        tvSaldoTersedia = findViewById(R.id.tvSaldoTersedia)
        etNominal = findViewById(R.id.etNominalTarik)
        etTujuan = findViewById(R.id.etTujuanTarik)
        btnKonfirmasi = findViewById(R.id.btnKonfirmasiTarik)

        val sharedPref = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        userId = sharedPref.getString("USER_ID", null)

        if (userId == null) {
            Toast.makeText(this, "Sesi berakhir, silakan login ulang", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        dbRef = FirebaseDatabase.getInstance().reference
        
        // Ambil saldo real-time
        fetchCurrentPoin()

        btnKonfirmasi.setOnClickListener {
            val nominalStr = etNominal.text.toString()
            val tujuan = etTujuan.text.toString()

            if (nominalStr.isEmpty() || tujuan.isEmpty()) {
                Toast.makeText(this, "Harap isi semua kolom", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val nominal = nominalStr.toInt()

            if (nominal < 10000) {
                Toast.makeText(this, "Minimal penarikan 10.000 poin", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (nominal > currentPoin) {
                Toast.makeText(this, "Saldo tidak mencukupi", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lakukanPenarikan(nominal, tujuan)
        }
    }

    private fun fetchCurrentPoin() {
        dbRef.child("users").child(userId!!).child("poin")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    currentPoin = snapshot.getValue(Int::class.java) ?: 0
                    tvSaldoTersedia.text = "Poin ${String.format("%,d", currentPoin)}"
                }
                override fun onCancelled(error: DatabaseError) {}
            })
    }

    private fun lakukanPenarikan(nominal: Int, tujuan: String) {
        val userPoinRef = dbRef.child("users").child(userId!!).child("poin")

        userPoinRef.runTransaction(object : Transaction.Handler {
            override fun doTransaction(mutableData: MutableData): Transaction.Result {
                val poinSekarang = mutableData.getValue(Int::class.java) ?: 0
                if (poinSekarang < nominal) {
                    return Transaction.abort()
                }
                mutableData.value = poinSekarang - nominal
                return Transaction.success(mutableData)
            }

            override fun onComplete(error: DatabaseError?, committed: Boolean, snapshot: DataSnapshot?) {
                if (committed) {
                    catatRiwayatPenarikan(nominal, tujuan)
                    Toast.makeText(this@TarikSaldoActivity, "Penarikan berhasil diproses!", Toast.LENGTH_LONG).show()
                    
                    // Navigasi ke halaman riwayat (tab Mutasi di MainActivity)
                    val intent = Intent(this@TarikSaldoActivity, MainActivity::class.java).apply {
                        putExtra("OPEN_TAB", "MUTASI")
                        flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this@TarikSaldoActivity, "Gagal: ${error?.message}", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun catatRiwayatPenarikan(nominal: Int, tujuan: String) {
        val riwayatRef = dbRef.child("riwayat_transaksi").push()
        val tanggal = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()).format(Date())
        
        val dataRiwayat = hashMapOf(
            "userId" to userId,
            "tanggal" to tanggal,
            "deskripsi" to "Penarikan ke $tujuan",
            "poin" to "-${String.format("%,d", nominal)}",
            "isMasuk" to false
        )
        riwayatRef.setValue(dataRiwayat)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}
