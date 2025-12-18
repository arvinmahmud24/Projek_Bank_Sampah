package com.example.myapplication.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.myapplication.CariBankSampahFragment
import com.example.myapplication.KatalogHargaActivity
import com.example.myapplication.RiwayatActivity
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private var isSaldoVisible = false
    private val realSaldo = "15.250"
    private val hiddenSaldo = "••••••"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val username = arguments?.getString("USERNAME")
        val email = arguments?.getString("EMAIL")
        binding.textViewUsername.text = username

        // Inisialisasi Tampilan Saldo
        binding.tvSaldo.text = hiddenSaldo
        
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

        binding.buttonCariBankSampah.setOnClickListener {
            val fragment = CariBankSampahFragment().apply {
                arguments = Bundle().apply {
                    putString("USERNAME", username)
                    putString("EMAIL", email)
                }
            }
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit()
        }

        binding.buttonKatalogHarga.setOnClickListener {
            val intent = Intent(activity, KatalogHargaActivity::class.java).apply {
                putExtra("USERNAME", username)
                putExtra("EMAIL", email)
            }
            startActivity(intent)
        }

        // Listener untuk tombol riwayat pada card saldo
        binding.layoutLihatRiwayat.setOnClickListener {
            val intent = Intent(activity, RiwayatActivity::class.java)
            startActivity(intent)
        }

        // Listener untuk tombol menu riwayat (jika ID di XML adalah button_pilih_bank_sampah)
        binding.buttonPilihBankSampah.setOnClickListener {
            val intent = Intent(activity, RiwayatActivity::class.java)
            startActivity(intent)
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}