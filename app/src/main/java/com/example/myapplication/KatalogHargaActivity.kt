package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import java.text.NumberFormat
import java.util.Locale

data class SampahType(val nama: String = "", val unit: String = "")

class KatalogHargaActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var sampahAdapter: SampahAdapter
    private val sampahList = mutableListOf<Sampah>()
    private lateinit var dbRefHarga: DatabaseReference
    private lateinit var dbRefSampahTypes: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_katalog_harga)

        recyclerView = findViewById(R.id.recyclerViewSampah)
        recyclerView.layoutManager = LinearLayoutManager(this)
        sampahAdapter = SampahAdapter(sampahList) { sampah ->
            val intent = Intent(this, KalkulatorHargaActivity::class.java).apply {
                putExtra("NAMA_SAMPAH", sampah.nama)
                putExtra("HARGA_SAMPAH", sampah.harga)
            }
            startActivity(intent)
        }
        recyclerView.adapter = sampahAdapter

        val bankSampahId = intent.getStringExtra("BANK_SAMPAH_ID")
        if (bankSampahId.isNullOrEmpty()) {
            Toast.makeText(this, "ID Bank Sampah tidak valid.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        dbRefHarga = FirebaseDatabase.getInstance().getReference("harga").child(bankSampahId)
        dbRefSampahTypes = FirebaseDatabase.getInstance().getReference("sampah_types")

        fetchHarga()
    }

    private fun fetchHarga() {
        dbRefHarga.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                sampahList.clear()
                if (!dataSnapshot.exists()) {
                    Toast.makeText(
                        this@KatalogHargaActivity,
                        "Katalog harga untuk bank sampah ini belum tersedia.",
                        Toast.LENGTH_LONG
                    ).show()
                    sampahAdapter.notifyDataSetChanged()
                    return
                }

                dataSnapshot.children.forEach { hargaSnapshot ->
                    val sampahId = hargaSnapshot.key
                    val harga = hargaSnapshot.getValue(Long::class.java)

                    if (sampahId != null && harga != null) {
                        dbRefSampahTypes.child(sampahId).addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(typeSnapshot: DataSnapshot) {
                                val sampahType = typeSnapshot.getValue(SampahType::class.java)
                                if (sampahType != null) {
                                    val namaLengkap = "${sampahType.nama} (${sampahType.unit})"
                                    val hargaFormatted = NumberFormat.getCurrencyInstance(Locale("in", "ID")).format(harga)
                                    sampahList.add(Sampah(namaLengkap, hargaFormatted))
                                    sampahAdapter.notifyDataSetChanged()
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                Toast.makeText(this@KatalogHargaActivity, "Gagal memuat detail sampah", Toast.LENGTH_SHORT).show()
                            }
                        })
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(this@KatalogHargaActivity, "Gagal memuat data harga", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
