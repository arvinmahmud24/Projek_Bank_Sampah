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

        // Setup RecyclerView
        binding.rvTransaksi.layoutManager = LinearLayoutManager(context)
        val adapter = TransaksiAdapter(transaksiList)
        binding.rvTransaksi.adapter = adapter

        // Hubungkan ke Firebase
        dbRef = FirebaseDatabase.getInstance().getReference("riwayat_transaksi")
        
        fetchRiwayatData(adapter)
    }

    private fun fetchRiwayatData(adapter: TransaksiAdapter) {
        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                transaksiList.clear()
                if (snapshot.exists()) {
                    for (transaksiSnapshot in snapshot.children) {
                        val transaksi = transaksiSnapshot.getValue(Transaksi::class.java)
                        if (transaksi != null) {
                            transaksiList.add(transaksi)
                        }
                    }
                    // Balik list agar yang terbaru ada di paling atas
                    transaksiList.reverse()
                    adapter.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Gagal memuat data: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
