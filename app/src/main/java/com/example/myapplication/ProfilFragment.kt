package com.example.myapplication

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.myapplication.databinding.DialogEditProfilBinding
import com.example.myapplication.databinding.FragmentProfilBinding
import com.example.myapplication.ui.LoginActivity

class ProfilFragment : Fragment() {

    private var _binding: FragmentProfilBinding? = null
    private val binding get() = _binding!!
    
    private var tempImageUri: Uri? = null
    private var dialogBinding: DialogEditProfilBinding? = null

    private val galleryLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            val selectedUri = result.data?.data
            if (selectedUri != null) {
                tempImageUri = selectedUri
                dialogBinding?.dialogImageFoto?.setImageURI(selectedUri)
                if (dialogBinding == null) {
                    binding.imageFotoProfil.setImageURI(selectedUri)
                    Toast.makeText(context, "Foto Profil Diperbarui", Toast.LENGTH_SHORT).show()
                }
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
        binding.menuEditProfil.setOnClickListener { tampilkanDialogEditProfil() }
        
        binding.menuBantuan.setOnClickListener { 
            val intent = Intent(activity, BantuanActivity::class.java)
            startActivity(intent)
        }
        
        // Logika Keluar (Logout) ke halaman Login
        binding.menuKeluar.setOnClickListener { 
            val intent = Intent(requireContext(), LoginActivity::class.java)
            // Hapus stack activity agar user tidak bisa kembali ke halaman sebelumnya
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            requireActivity().finish()
            Toast.makeText(context, "Berhasil Keluar", Toast.LENGTH_SHORT).show() 
        }
    }

    private fun bukaGaleri() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galleryLauncher.launch(intent)
    }

    private fun tampilkanDialogEditProfil() {
        dialogBinding = DialogEditProfilBinding.inflate(LayoutInflater.from(requireContext()))
        dialogBinding!!.dialogEtNama.setText(binding.tvNamaProfil.text)

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogBinding!!.root)
            .setPositiveButton("Simpan") { _, _ ->
                val namaBaru = dialogBinding!!.dialogEtNama.text.toString()
                if (namaBaru.isNotEmpty()) {
                    binding.tvNamaProfil.text = namaBaru
                    tempImageUri?.let { binding.imageFotoProfil.setImageURI(it) }
                    Toast.makeText(context, "Profil diperbarui!", Toast.LENGTH_SHORT).show()
                }
                dialogBinding = null
                tempImageUri = null
            }
            .setNegativeButton("Batal") { _, _ ->
                dialogBinding = null
                tempImageUri = null
            }
            .create()

        dialogBinding!!.dialogCardFoto.setOnClickListener { bukaGaleri() }
        dialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        dialogBinding = null
    }
}
