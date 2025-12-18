package com.example.myapplication

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class RiwayatActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TransaksiAdapter
    private val transaksiList = mutableListOf<Transaksi>()
    private lateinit var tvNoData: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_riwayat)

        recyclerView = findViewById(R.id.recyclerViewRiwayat)
        tvNoData = findViewById(R.id.tv_no_data)

        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = TransaksiAdapter(transaksiList)
        recyclerView.adapter = adapter

        fetchRiwayatTransaksi()
    }

    private fun fetchRiwayatTransaksi() {
        val dbRef = FirebaseDatabase.getInstance().getReference("transaksi")

        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                transaksiList.clear()
                if (snapshot.exists()) {
                    for (transaksiSnapshot in snapshot.children) {
                        try {
                            val tanggal = transaksiSnapshot.child("tanggal").getValue(String::class.java) ?: ""
                            val deskripsi = transaksiSnapshot.child("deskripsi").getValue(String::class.java) ?: ""
                            val poin = transaksiSnapshot.child("poin").getValue(String::class.java) ?: ""
                            val isMasuk = transaksiSnapshot.child("isMasuk").getValue(Boolean::class.java) ?: true

                            val transaksi = Transaksi(tanggal, deskripsi, poin, isMasuk)
                            transaksiList.add(transaksi)
                        } catch (e: Exception) {
                            Log.e("RiwayatActivity", "Error parsing data: ${e.message}")
                        }
                    }
                    
                    if (transaksiList.isEmpty()) {
                        tvNoData.visibility = View.VISIBLE
                        recyclerView.visibility = View.GONE
                    } else {
                        transaksiList.reverse() // Tampilkan yang terbaru di atas
                        adapter.notifyDataSetChanged()
                        tvNoData.visibility = View.GONE
                        recyclerView.visibility = View.VISIBLE
                    }
                } else {
                    tvNoData.visibility = View.VISIBLE
                    recyclerView.visibility = View.GONE
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("RiwayatActivity", "Database error: ${error.message}")
                tvNoData.text = "Gagal memuat data"
                tvNoData.visibility = View.VISIBLE
                recyclerView.visibility = View.GONE
            }
        })
    }
}