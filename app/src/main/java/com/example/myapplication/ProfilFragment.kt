package com.example.myapplication

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.myapplication.databinding.FragmentProfilBinding

class ProfilFragment : Fragment() {

    private var _binding: FragmentProfilBinding? = null
    private val binding get() = _binding!!

    private val galleryLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            val selectedImageUri = result.data?.data
            if (selectedImageUri != null) {
                binding.imageFotoProfil.setImageURI(selectedImageUri)
                Toast.makeText(context, "Foto Profil Diperbarui", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfilBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            binding.tvNamaProfil.text = it.getString("USERNAME")
            binding.tvEmailProfil.text = it.getString("EMAIL")
        }

        binding.cardFotoProfil.setOnClickListener { bukaGaleri() }
        binding.menuEditProfil.setOnClickListener { tampilkanDialogEditNama() }
        binding.menuRiwayat.setOnClickListener { Toast.makeText(context, "Fitur Riwayat akan segera hadir", Toast.LENGTH_SHORT).show() }
        binding.menuBantuan.setOnClickListener { Toast.makeText(context, "Menghubungi Pusat Bantuan...", Toast.LENGTH_SHORT).show() }
        binding.menuKeluar.setOnClickListener { Toast.makeText(context, "Berhasil Keluar", Toast.LENGTH_SHORT).show() }
    }

    private fun bukaGaleri() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galleryLauncher.launch(intent)
    }

    private fun tampilkanDialogEditNama() {
        val container = FrameLayout(requireContext()).apply {
            val params = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
                leftMargin = 50
                rightMargin = 50
            }
            layoutParams = params
        }

        val input = EditText(context).apply {
            setText(binding.tvNamaProfil.text)
        }
        container.addView(input)

        AlertDialog.Builder(requireContext())
            .setTitle("Ubah Nama Profil")
            .setMessage("Masukkan nama baru Anda:")
            .setView(container)
            .setPositiveButton("Simpan") { _, _ ->
                val namaBaru = input.text.toString()
                if (namaBaru.isNotEmpty()) {
                    binding.tvNamaProfil.text = namaBaru
                    Toast.makeText(context, "Nama berhasil diubah!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Nama tidak boleh kosong", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
