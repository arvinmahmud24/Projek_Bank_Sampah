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

        binding.buttonRiwayat.setOnClickListener {
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