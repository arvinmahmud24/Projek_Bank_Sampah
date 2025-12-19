package com.example.myapplication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.databinding.FragmentTransaksiBinding
import com.google.firebase.database.*
import java.text.NumberFormat
import java.util.Locale

class TransaksiFragment : Fragment() {

    private var _binding: FragmentTransaksiBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var dbRef: DatabaseReference
    private val transaksiList = mutableListOf<Transaksi>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTransaksiBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inisialisasi RecyclerView
        binding.rvTransaksi.layoutManager = LinearLayoutManager(context)
        val adapter = TransaksiAdapter(transaksiList)
        binding.rvTransaksi.adapter = adapter

        // Referensi database riwayat_transaksi
        dbRef = FirebaseDatabase.getInstance().getReference("riwayat_transaksi")
        
        fetchRiwayatData(adapter)
    }

    private fun fetchRiwayatData(adapter: TransaksiAdapter) {
        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                transaksiList.clear()
                var totalMasuk = 0
                var totalKeluar = 0

                if (snapshot.exists()) {
                    for (transaksiSnapshot in snapshot.children) {
                        val transaksi = transaksiSnapshot.getValue(Transaksi::class.java)
                        if (transaksi != null) {
                            transaksiList.add(transaksi)
                            
                            // Logika Kalkulasi: Bersihkan string poin agar menjadi angka murni
                            val nominal = transaksi.poin.replace(Regex("[^0-9]"), "").toIntOrNull() ?: 0
                            
                            if (transaksi.isMasuk) {
                                totalMasuk += nominal
                            } else {
                                totalKeluar += nominal
                            }
                        }
                    }
                    
                    // Update tampilan statistik di bagian atas
                    updateSummary(totalMasuk, totalKeluar)
                    
                    // Urutkan dari yang terbaru
                    transaksiList.reverse()
                    adapter.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                context?.let {
                    Toast.makeText(it, "Gagal memuat data: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun updateSummary(masuk: Int, keluar: Int) {
        val formatRupiah = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
        
        // Menampilkan Total Masuk (Hijau)
        binding.tvTotalPemasukan.text = formatRupiah.format(masuk).replace("Rp", "pts ").trim()
        
        // Menampilkan Total Keluar (Merah)
        binding.tvTotalPengeluaran.text = formatRupiah.format(keluar).replace("Rp", "pts ").trim()
        
        // Menampilkan Selisih (Hitam/Hijau Gelap)
        val selisih = masuk - keluar
        binding.tvSelisihSaldo.text = formatRupiah.format(selisih).replace("Rp", "pts ").trim()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
