package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class KatalogHargaActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private SampahAdapter adapter;
    private List<Sampah> sampahList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_katalog_harga);

        recyclerView = findViewById(R.id.recyclerViewSampah);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        sampahList = new ArrayList<>();
        
        // Data Katalog & Harga TERBARU (Harga Jual Sampah Saat Ini)
        sampahList.add(new Sampah("Minyak Jelantah (Liter)", "Rp. 5,000"));
        sampahList.add(new Sampah("Emberan warna (emberan) (Kg)", "Rp. 960"));
        sampahList.add(new Sampah("Kertas putihan (Kg)", "Rp. 1,120"));
        sampahList.add(new Sampah("Kardus (Kg)", "Rp. 1,200"));
        sampahList.add(new Sampah("Duplex (Kg)", "Rp. 480"));
        sampahList.add(new Sampah("STAL/ KABIN/ ENAMEL (Kg)", "Rp. 1,600"));
        sampahList.add(new Sampah("Alumunium rongsok (Kg)", "Rp. 6,400"));
        sampahList.add(new Sampah("Botol Pet A (tanpa label dan tutup) (Kg)", "Rp. 3,200"));
        sampahList.add(new Sampah("Sepatu dan sandal (Kg)", "Rp. 40"));
        sampahList.add(new Sampah("Paralon (Kg)", "Rp. 560"));
        sampahList.add(new Sampah("Kaleng (Kg)", "Rp. 1,440"));
        sampahList.add(new Sampah("Aki motor/ mobil (Unit)", "Rp. 5,200"));
        sampahList.add(new Sampah("Mesin cuci 1 tabung (Unit)", "Rp. 24,000"));
        sampahList.add(new Sampah("Besi A (Kg)", "Rp. 2,400"));
        sampahList.add(new Sampah("Keping CD (Kg)", "Rp. 2,400"));
        sampahList.add(new Sampah("Mesin cuci 2 Tabung (Unit)", "Rp. 28,000"));
        sampahList.add(new Sampah("Kulkas/ lemari es (Unit)", "Rp. 32,000"));
        sampahList.add(new Sampah("Buku pelajaran (Kg)", "Rp. 800"));
        sampahList.add(new Sampah("Monitor/ TV Tabung (Unit)", "Rp. 8,000"));
        sampahList.add(new Sampah("Plastik asoy (kresek assoy) (Kg)", "Rp. 160"));
        sampahList.add(new Sampah("Selopan (Kg)", "Rp. 120"));
        sampahList.add(new Sampah("Galon isi ulang (Unit)", "Rp. 3,200"));
        sampahList.add(new Sampah("Botol Pet C (botol plastik bekas kecap) (Kg)", "Rp. 400"));
        sampahList.add(new Sampah("Kertas semen (Kg)", "Rp. 800"));
        sampahList.add(new Sampah("Alumunium plat/ panci (Kg)", "Rp. 7,200"));
        sampahList.add(new Sampah("Tembaga kupas (Kg)", "Rp. 40,000"));
        sampahList.add(new Sampah("Tutup HD (bekas tutup botol air) (Kg)", "Rp. 1,600"));
        sampahList.add(new Sampah("Tutup LD (bekas tutup botol galon merk) (Kg)", "Rp. 2,000"));
        sampahList.add(new Sampah("Laptop/ notebook/ LCD/ LED (Kg)", "Rp. 16,000"));
        sampahList.add(new Sampah("Botol Pet B (ada label dan tutup) (Kg)", "Rp. 960"));
        sampahList.add(new Sampah("Emberan hitam (Kg)", "Rp. 560"));
        sampahList.add(new Sampah("Gelas warna (mountea, ale2, dll) (Kg)", "Rp. 1,040"));
        sampahList.add(new Sampah("Gelas plastik A (Kg)", "Rp. 2,480"));
        sampahList.add(new Sampah("Smartphone/ android/ap (Unit)", "Rp. 2,000"));
        sampahList.add(new Sampah("Botol Pet Warna (botol air mineral warna) (Kg)", "Rp. 800"));
        sampahList.add(new Sampah("Botol Pet Putih (botol yang berwarna putih) (Kg)", "Rp. 400"));
        sampahList.add(new Sampah("Gelas Plastik B (Kg)", "Rp. 1,200"));
        sampahList.add(new Sampah("Botol Plastik HDPE (bekas shampoo) (Kg)", "Rp. 1,600"));
        sampahList.add(new Sampah("Botol plastik NASO (HDPE putih) (Kg)", "Rp. 2,400"));
        sampahList.add(new Sampah("Plastik PP Inject (plastik PP berwarna) (Kg)", "Rp. 2,400"));
        sampahList.add(new Sampah("Emberan bening/ kristal (bekas kue nastar) (Kg)", "Rp. 2,800"));
        sampahList.add(new Sampah("Galon le minerale (Kg)", "Rp. 2,560"));
        sampahList.add(new Sampah("Botol yakult (Kg)", "Rp. 320"));
        sampahList.add(new Sampah("Impact kasar (Kg)", "Rp. 400"));
        sampahList.add(new Sampah("PE Putih (plastik berwarna) (Kg)", "Rp. 160"));
        sampahList.add(new Sampah("Mika (Plastik Mika) (Kg)", "Rp. 40"));
        sampahList.add(new Sampah("Plastik sedotan (Kg)", "Rp. 160"));
        sampahList.add(new Sampah("Multilayer (Kg)", "Rp. 40"));
        sampahList.add(new Sampah("Koran (Kg)", "Rp. 800"));
        sampahList.add(new Sampah("Kertas Warna (Kg)", "Rp. 480"));
        sampahList.add(new Sampah("LKS (Kg)", "Rp. 800"));
        sampahList.add(new Sampah("Tetra Pack (Kg)", "Rp. 80"));
        sampahList.add(new Sampah("Tempat Telor (Kg)", "Rp. 160"));
        sampahList.add(new Sampah("HP GSM Biasa (Kg)", "Rp. 4,000"));
        sampahList.add(new Sampah("Besi Campur (Kg)", "Rp. 1,600"));
        sampahList.add(new Sampah("Babet (onderdil motor/ mobil) (Kg)", "Rp. 4,800"));
        sampahList.add(new Sampah("Tembaga bakar (Kg)", "Rp. 32,000"));
        sampahList.add(new Sampah("Kuningan (Kg)", "Rp. 24,000"));
        sampahList.add(new Sampah("Botol Beling (Kg)", "Rp. 160"));
        sampahList.add(new Sampah("Nilex (Kg)", "Rp. 160"));
        sampahList.add(new Sampah("Gabrukan (Kg)", "Rp. 400"));
        sampahList.add(new Sampah("Botol oli (Kg)", "Rp. 1,600"));
        sampahList.add(new Sampah("Kawat spring bed (Kg)", "Rp. 400"));

        // Membuat Adapter dengan Listener untuk membuka Kalkulator
        adapter = new SampahAdapter(sampahList, new SampahAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Sampah sampah) {
                Intent intent = new Intent(KatalogHargaActivity.this, KalkulatorHargaActivity.class);
                intent.putExtra("NAMA_SAMPAH", sampah.getNama());
                intent.putExtra("HARGA_SAMPAH", sampah.getHarga());
                startActivity(intent);
            }
        });

        recyclerView.setAdapter(adapter);
    }
}
