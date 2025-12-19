package com.example.myapplication

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class KatalogHargaActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: SampahAdapter
    private val sampahList = mutableListOf<Sampah>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_katalog_harga)

        recyclerView = findViewById(R.id.recyclerViewSampah)
        recyclerView.layoutManager = LinearLayoutManager(this)

        populateSampahList()

        // Inisialisasi adapter dalam mode Read-Only (Hanya tampilkan harga)
        adapter = SampahAdapter(sampahList, isReadOnly = true)
        recyclerView.adapter = adapter
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
