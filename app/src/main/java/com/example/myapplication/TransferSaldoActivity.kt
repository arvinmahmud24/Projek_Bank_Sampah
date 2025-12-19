package com.example.myapplication

import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*

class TransferSaldoActivity : AppCompatActivity() {

    private lateinit var etUsernameTujuan: EditText
    private lateinit var etNominal: EditText
    private lateinit var btnTransfer: Button
    
    private var userId: String? = null
    private var myUsername: String? = null
    private var myPoin: Int = 0
    private lateinit var dbRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transfer_saldo)

        val toolbar: Toolbar = findViewById(R.id.toolbarTransfer)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        etUsernameTujuan = findViewById(R.id.etUsernameTujuan)
        etNominal = findViewById(R.id.etNominalTransfer)
        btnTransfer = findViewById(R.id.btnKonfirmasiTransfer)

        val sharedPref = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        userId = sharedPref.getString("USER_ID", null)
        myUsername = sharedPref.getString("USERNAME", null)

        if (userId == null) {
            Toast.makeText(this, "Sesi berakhir, silakan login ulang", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        dbRef = FirebaseDatabase.getInstance().reference
        
        // Pantau saldo pengirim
        fetchMyPoin()

        btnTransfer.setOnClickListener {
            val targetUsername = etUsernameTujuan.text.toString().trim()
            val nominalStr = etNominal.text.toString()

            if (targetUsername.isEmpty() || nominalStr.isEmpty()) {
                Toast.makeText(this, "Harap isi semua kolom", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (targetUsername == myUsername) {
                Toast.makeText(this, "Tidak bisa kirim ke diri sendiri", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val nominal = nominalStr.toInt()
            if (nominal <= 0) {
                Toast.makeText(this, "Nominal tidak valid", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (nominal > myPoin) {
                Toast.makeText(this, "Saldo poin tidak mencukupi", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            prosesTransfer(targetUsername, nominal)
        }
    }

    private fun fetchMyPoin() {
        dbRef.child("users").child(userId!!).child("poin")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    myPoin = snapshot.getValue(Int::class.java) ?: 0
                }
                override fun onCancelled(error: DatabaseError) {}
            })
    }

    private fun prosesTransfer(targetUsername: String, nominal: Int) {
        // 1. Cari User Tujuan berdasarkan Username
        dbRef.child("users").orderByChild("username").equalTo(targetUsername)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val targetUserSnapshot = snapshot.children.first()
                        val targetUserId = targetUserSnapshot.key
                        
                        if (targetUserId != null) {
                            lakukanTransaksiTransfer(targetUserId, targetUsername, nominal)
                        }
                    } else {
                        Toast.makeText(this@TransferSaldoActivity, "Username penerima tidak ditemukan", Toast.LENGTH_SHORT).show()
                    }
                }
                override fun onCancelled(error: DatabaseError) {}
            })
    }

    private fun lakukanTransaksiTransfer(targetUserId: String, targetUsername: String, nominal: Int) {
        // Kurangi poin pengirim
        val myPoinRef = dbRef.child("users").child(userId!!).child("poin")
        val targetPoinRef = dbRef.child("users").child(targetUserId).child("poin")

        myPoinRef.runTransaction(object : Transaction.Handler {
            override fun doTransaction(mutableData: MutableData): Transaction.Result {
                val current = mutableData.getValue(Int::class.java) ?: 0
                if (current < nominal) return Transaction.abort()
                mutableData.value = current - nominal
                return Transaction.success(mutableData)
            }

            override fun onComplete(error: DatabaseError?, committed: Boolean, snapshot: DataSnapshot?) {
                if (committed) {
                    // Tambah poin penerima
                    targetPoinRef.runTransaction(object : Transaction.Handler {
                        override fun doTransaction(mutableData: MutableData): Transaction.Result {
                            val current = mutableData.getValue(Int::class.java) ?: 0
                            mutableData.value = current + nominal
                            return Transaction.success(mutableData)
                        }
                        override fun onComplete(e: DatabaseError?, c: Boolean, s: DataSnapshot?) {
                            catatRiwayatTransfer(targetUsername, nominal)
                            Toast.makeText(this@TransferSaldoActivity, "Transfer Berhasil!", Toast.LENGTH_LONG).show()
                            finish()
                        }
                    })
                }
            }
        })
    }

    private fun catatRiwayatTransfer(targetName: String, nominal: Int) {
        val riwayatRef = dbRef.child("riwayat_transaksi").push()
        val tanggal = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()).format(Date())
        
        val dataRiwayat = hashMapOf(
            "userId" to userId,
            "tanggal" to tanggal,
            "deskripsi" to "Transfer ke $targetName",
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
