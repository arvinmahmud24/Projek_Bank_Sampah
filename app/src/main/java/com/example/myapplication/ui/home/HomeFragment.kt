// Fixed R class ambiguity and missing Context import
package com.example.myapplication.ui.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.myapplication.CariBankSampahFragment
import com.example.myapplication.KatalogHargaActivity
import com.example.myapplication.MainActivity
import com.example.myapplication.R
import com.example.myapplication.TarikSaldoActivity
import com.example.myapplication.databinding.FragmentHomeBinding
import com.google.firebase.database.*

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private var isSaldoVisible = false
    private var realSaldo = "0"
    private val hiddenSaldo = "••••••"

    private lateinit var dbRef: DatabaseReference
    private var userId: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Ambil data dari SharedPreferences sebagai fallback utama agar sesi tidak hilang
        val sharedPref = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        userId = arguments?.getString("USER_ID") ?: sharedPref.getString("USER_ID", null)
        val username = arguments?.getString("USERNAME") ?: sharedPref.getString("USERNAME", null)
        val email = arguments?.getString("EMAIL") ?: sharedPref.getString("EMAIL", null)

        binding.textViewUsername.text = username ?: "User"

        // Inisialisasi Tampilan Saldo
        binding.tvSaldo.text = hiddenSaldo

        // Hubungkan ke Firebase untuk mengambil poin real-time
        if (userId != null) {
            dbRef = FirebaseDatabase.getInstance().getReference("users").child(userId!!)
            dbRef.child("poin").addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val poin = snapshot.getValue(Int::class.java) ?: 0
                    realSaldo = String.format("%,d", poin)

                    // Update tampilan jika sedang visible
                    if (isSaldoVisible) {
                        binding.tvSaldo.text = realSaldo
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle error
                }
            })
        }

        binding.ivToggleSaldo.setOnClickListener {
            isSaldoVisible = !isSaldoVisible
            if (isSaldoVisible) {
                binding.tvSaldo.text = realSaldo
                binding.ivToggleSaldo.setImageResource(R.drawable.ic_visibility)
            } else {
                binding.tvSaldo.text = hiddenSaldo
                binding.ivToggleSaldo.setImageResource(R.drawable.ic_visibility_off)
            }
        }

        // Row 1: Cari Bank Sampah
        binding.buttonCariBankSampah.setOnClickListener {
            val fragment = CariBankSampahFragment().apply {
                arguments = Bundle().apply {
                    putString("USER_ID", userId)
                    putString("USERNAME", username)
                    putString("EMAIL", email)
                }
            }
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit()
        }

        // Row 2: Katalog & Harga
        binding.buttonKatalogHarga.setOnClickListener {
            val intent = Intent(requireContext(), KatalogHargaActivity::class.java).apply {
                putExtra("USER_ID", userId)
                putExtra("USERNAME", username)
                putExtra("EMAIL", email)
            }
            startActivity(intent)
        }

        // Link Riwayat dari footer Saldo
        binding.layoutLihatRiwayat.setOnClickListener {
            (activity as? MainActivity)?.setSelectedTab(R.id.nav_transaction)
        }

        // Row 3: Tarik Saldo
        binding.buttonTarikSaldo.setOnClickListener {
            val intent = Intent(requireContext(), TarikSaldoActivity::class.java)
            startActivity(intent)
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
