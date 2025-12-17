package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class KatalogHargaActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: SampahAdapter
    private lateinit var sampahList: MutableList<Sampah>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_katalog_harga)

        recyclerView = findViewById(R.id.recyclerViewSampah)
        recyclerView.layoutManager = LinearLayoutManager(this)

        sampahList = mutableListOf()
        populateSampahList()

        // Buka KalkulatorActivity saat item diklik
        adapter = SampahAdapter(sampahList) { sampah ->
            val intent = Intent(this, KalkulatorActivity::class.java).apply {
                putExtra("NAMA_SAMPAH", sampah.nama)
                putExtra("HARGA_SAMPAH", sampah.harga)
            }
            startActivity(intent)
        }
        recyclerView.adapter = adapter
    }

    private fun populateSampahList() {
        sampahList.add(Sampah("Minyak Jelantah (Liter)", "Rp. 5,000"))
        sampahList.add(Sampah("Emberan warna (emberan) (Kg)", "Rp. 960"))
        sampahList.add(Sampah("Kertas putihan (Kg)", "Rp. 1,120"))
        sampahList.add(Sampah("Kardus (Kg)", "Rp. 1,200"))
        sampahList.add(Sampah("Duplex (Kg)", "Rp. 480"))
        sampahList.add(Sampah("STAL/ KABIN/ ENAMEL (Kg)", "Rp. 1,600"))
        sampahList.add(Sampah("Alumunium rongsok (Kg)", "Rp. 6,400"))
        sampahList.add(Sampah("Botol Pet A (tanpa label dan tutup) (Kg)", "Rp. 3,200"))
        sampahList.add(Sampah("Sepatu dan sandal (Kg)", "Rp. 40"))
        sampahList.add(Sampah("Paralon (Kg)", "Rp. 560"))
        sampahList.add(Sampah("Kaleng (Kg)", "Rp. 1,440"))
        sampahList.add(Sampah("Aki motor/ mobil (Unit)", "Rp. 5,200"))
        sampahList.add(Sampah("Mesin cuci 1 tabung (Unit)", "Rp. 24,000"))
        sampahList.add(Sampah("Besi A (Kg)", "Rp. 2,400"))
        sampahList.add(Sampah("Keping CD (Kg)", "Rp. 2,400"))
        sampahList.add(Sampah("Mesin cuci 2 Tabung (Unit)", "Rp. 28,000"))
        sampahList.add(Sampah("Kulkas/ lemari es (Unit)", "Rp. 32,000"))
        sampahList.add(Sampah("Buku pelajaran (Kg)", "Rp. 800"))
        sampahList.add(Sampah("Monitor/ TV Tabung (Unit)", "Rp. 8,000"))
        sampahList.add(Sampah("Plastik asoy (kresek assoy) (Kg)", "Rp. 160"))
        sampahList.add(Sampah("Selopan (Kg)", "Rp. 120"))
        sampahList.add(Sampah("Galon isi ulang (Unit)", "Rp. 3,200"))
        sampahList.add(Sampah("Botol Pet C (botol plastik bekas kecap) (Kg)", "Rp. 400"))
        sampahList.add(Sampah("Kertas semen (Kg)", "Rp. 800"))
        sampahList.add(Sampah("Alumunium plat/ panci (Kg)", "Rp. 7,200"))
        sampahList.add(Sampah("Tembaga kupas (Kg)", "Rp. 40,000"))
        sampahList.add(Sampah("Tutup HD (bekas tutup botol air) (Kg)", "Rp. 1,600"))
        sampahList.add(Sampah("Tutup LD (bekas tutup botol galon merk) (Kg)", "Rp. 2,000"))
        sampahList.add(Sampah("Laptop/ notebook/ LCD/ LED (Kg)", "Rp. 16,000"))
        sampahList.add(Sampah("Botol Pet B (ada label dan tutup) (Kg)", "Rp. 960"))
        sampahList.add(Sampah("Emberan hitam (Kg)", "Rp. 560"))
        sampahList.add(Sampah("Gelas warna (mountea, ale2, dll) (Kg)", "Rp. 1,040"))
        sampahList.add(Sampah("Gelas plastik A (Kg)", "Rp. 2,480"))
        sampahList.add(Sampah("Smartphone/ android/ap (Unit)", "Rp. 2,000"))
        sampahList.add(Sampah("Botol Pet Warna (botol air mineral warna) (Kg)", "Rp. 800"))
        sampahList.add(Sampah("Botol Pet Putih (botol yang berwarna putih) (Kg)", "Rp. 400"))
        sampahList.add(Sampah("Gelas Plastik B (Kg)", "Rp. 1,200"))
        sampahList.add(Sampah("Botol Plastik HDPE (bekas shampoo) (Kg)", "Rp. 1,600"))
        sampahList.add(Sampah("Botol plastik NASO (HDPE putih) (Kg)", "Rp. 2,400"))
        sampahList.add(Sampah("Plastik PP Inject (plastik PP berwarna) (Kg)", "Rp. 2,400"))
        sampahList.add(Sampah("Emberan bening/ kristal (bekas kue nastar) (Kg)", "Rp. 2,800"))
        sampahList.add(Sampah("Galon le minerale (Kg)", "Rp. 2,560"))
        sampahList.add(Sampah("Botol yakult (Kg)", "Rp. 320"))
        sampahList.add(Sampah("Impact kasar (Kg)", "Rp. 400"))
        sampahList.add(Sampah("PE Putih (plastik berwarna) (Kg)", "Rp. 160"))
        sampahList.add(Sampah("Mika (Plastik Mika) (Kg)", "Rp. 40"))
        sampahList.add(Sampah("Plastik sedotan (Kg)", "Rp. 160"))
        sampahList.add(Sampah("Multilayer (Kg)", "Rp. 40"))
        sampahList.add(Sampah("Koran (Kg)", "Rp. 800"))
        sampahList.add(Sampah("Kertas Warna (Kg)", "Rp. 480"))
        sampahList.add(Sampah("LKS (Kg)", "Rp. 800"))
        sampahList.add(Sampah("Tetra Pack (Kg)", "Rp. 80"))
        sampahList.add(Sampah("Tempat Telor (Kg)", "Rp. 160"))
        sampahList.add(Sampah("HP GSM Biasa (Kg)", "Rp. 4,000"))
        sampahList.add(Sampah("Besi Campur (Kg)", "Rp. 1,600"))
        sampahList.add(Sampah("Babet (onderdil motor/ mobil) (Kg)", "Rp. 4,800"))
        sampahList.add(Sampah("Tembaga bakar (Kg)", "Rp. 32,000"))
        sampahList.add(Sampah("Kuningan (Kg)", "Rp. 24,000"))
        sampahList.add(Sampah("Botol Beling (Kg)", "Rp. 160"))
        sampahList.add(Sampah("Nilex (Kg)", "Rp. 160"))
        sampahList.add(Sampah("Gabrukan (Kg)", "Rp. 400"))
        sampahList.add(Sampah("Botol oli (Kg)", "Rp. 1,600"))
        sampahList.add(Sampah("Kawat spring bed (Kg)", "Rp. 400"))
    }
}
