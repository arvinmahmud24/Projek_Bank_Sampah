package com.example.myapplication;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

public class ProfilFragment extends Fragment {

    private ImageView imageFotoProfil;
    private TextView tvNamaProfil;
    private ActivityResultLauncher<Intent> galleryLauncher;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inisialisasi Launcher untuk Galeri (Ganti Foto)
        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri selectedImageUri = result.getData().getData();
                        if (selectedImageUri != null) {
                            imageFotoProfil.setImageURI(selectedImageUri);
                            Toast.makeText(getContext(), "Foto Profil Diperbarui", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profil, container, false);

        // Inisialisasi View
        CardView cardFotoProfil = view.findViewById(R.id.cardFotoProfil);
        imageFotoProfil = view.findViewById(R.id.imageFotoProfil);
        tvNamaProfil = view.findViewById(R.id.tvNamaProfil);
        
        LinearLayout menuEditProfil = view.findViewById(R.id.menuEditProfil);
        LinearLayout menuRiwayat = view.findViewById(R.id.menuRiwayat);
        LinearLayout menuBantuan = view.findViewById(R.id.menuBantuan);
        LinearLayout menuKeluar = view.findViewById(R.id.menuKeluar);

        // 1. Fitur Ganti Foto Profil (Klik pada area foto)
        cardFotoProfil.setOnClickListener(v -> bukaGaleri());

        // 2. Fitur Edit Nama Profil
        menuEditProfil.setOnClickListener(v -> tampilkanDialogEditNama());

        // 3. Menu Lainnya
        menuRiwayat.setOnClickListener(v -> 
            Toast.makeText(getContext(), "Fitur Riwayat akan segera hadir", Toast.LENGTH_SHORT).show());

        menuBantuan.setOnClickListener(v -> 
            Toast.makeText(getContext(), "Menghubungi Pusat Bantuan...", Toast.LENGTH_SHORT).show());

        menuKeluar.setOnClickListener(v -> 
            Toast.makeText(getContext(), "Berhasil Keluar", Toast.LENGTH_SHORT).show());

        return view;
    }

    private void bukaGaleri() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryLauncher.launch(intent);
    }

    private void tampilkanDialogEditNama() {
        // Container agar EditText punya margin yang rapi
        FrameLayout container = new FrameLayout(getContext());
        FrameLayout.LayoutParams params = new  FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.leftMargin = 50; // Margin kiri
        params.rightMargin = 50; // Margin kanan
        
        final EditText input = new EditText(getContext());
        input.setText(tvNamaProfil.getText());
        input.setLayoutParams(params);
        
        container.addView(input);

        new AlertDialog.Builder(getContext())
                .setTitle("Ubah Nama Profil")
                .setMessage("Masukkan nama baru Anda:")
                .setView(container) 
                .setPositiveButton("Simpan", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String namaBaru = input.getText().toString();
                        if (!namaBaru.isEmpty()) {
                            tvNamaProfil.setText(namaBaru);
                            Toast.makeText(getContext(), "Nama berhasil diubah!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "Nama tidak boleh kosong", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("Batal", null)
                .show();
    }
}
