package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public class HomeFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        LinearLayout buttonCariBankSampah = view.findViewById(R.id.button_cari_bank_sampah);
        buttonCariBankSampah.setOnClickListener(v -> {
            // Ganti dengan fragment CariBankSampahFragment
            FragmentManager fragmentManager = getParentFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, new CariBankSampahFragment())
                    .addToBackStack(null)
                    .commit();
        });

        // Listener untuk tombol lainnya bisa ditambahkan di sini

        return view;
    }
}
