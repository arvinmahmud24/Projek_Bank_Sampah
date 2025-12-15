package com.example.myapplication;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.tasks.CancellationTokenSource;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CariBankSampahFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private RecyclerView recyclerView;
    private BankSampahAdapter adapter;
    private List<BankSampah> bankSampahList;
    private FusedLocationProviderClient fusedLocationClient;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_cari_bank_sampah, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Inisialisasi Location Client
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        // Inisialisasi RecyclerView
        recyclerView = view.findViewById(R.id.recyclerViewBankSampah);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Inisialisasi Data Bank Sampah (DATA ASLI YOGYAKARTA)
        bankSampahList = new ArrayList<>();
        bankSampahList.add(new BankSampah("Bank Sampah Surolaras", "Jl. Suronatan No.Blok NG-2/51", new LatLng(-7.8005, 110.3610)));
        bankSampahList.add(new BankSampah("Bank Sampah Mondoroko RW 7", "Jl. Mondorakan No.27", new LatLng(-7.8286, 110.3957)));
        bankSampahList.add(new BankSampah("Bank Sampah Suryo Resik", "Mj 2/822, RT.44/RW.13", new LatLng(-7.8180, 110.3650)));
        bankSampahList.add(new BankSampah("Bank Sampah Tresno Tuhutentrem", "Jl. Sorosutan No.26", new LatLng(-7.8240, 110.3750)));
        bankSampahList.add(new BankSampah("Bank Sampah Sunten", "Kabupaten Bantul, DIY", new LatLng(-7.8350, 110.4100)));
        bankSampahList.add(new BankSampah("Bank Sampah Lestari RW.14", "Gg. Wiro Kresno", new LatLng(-7.7800, 110.3550)));
        bankSampahList.add(new BankSampah("Bank Sampah Induk Jogja", "Jl. Kemasan No.22", new LatLng(-7.8290, 110.4000)));
        bankSampahList.add(new BankSampah("Bank Sampah Mandiri", "Kabupaten Bantul, DIY", new LatLng(-7.8500, 110.3600)));
        bankSampahList.add(new BankSampah("Bank Sampah Gowok", "Kabupaten Sleman, DIY", new LatLng(-7.7830, 110.3950)));
        bankSampahList.add(new BankSampah("Bank sampah simul 5", "Jl. Sidomulyo No.345", new LatLng(-7.7900, 110.3500)));
        bankSampahList.add(new BankSampah("Bank Sampah Bedeng Berseri", "Jl. Bumijo Kulon No.I", new LatLng(-7.7850, 110.3620)));
        bankSampahList.add(new BankSampah("Bank Sampah Kusuma Pertiwi", "Jl. Ibu Ruswo No.35", new LatLng(-7.8040, 110.3680)));
        bankSampahList.add(new BankSampah("BSM Pandeyan", "UH 5 No.873 A", new LatLng(-7.8150, 110.3850)));
        bankSampahList.add(new BankSampah("Bank Sampah Blazent", "Jl. Taman Siswa No.7", new LatLng(-7.8080, 110.3780)));
        bankSampahList.add(new BankSampah("Bank Sampah Gemah Ripah Bantul", "Kabupaten Bantul, DIY", new LatLng(-7.9250, 110.3350)));
        bankSampahList.add(new BankSampah("Bank Sampah Resik lan Pikoleh", "Unnamed Road", new LatLng(-7.8800, 110.3400)));
        bankSampahList.add(new BankSampah("BANK SAMPAH BERLIAN 07", "Jl. Tompeyan TR III", new LatLng(-7.7850, 110.3550)));
        bankSampahList.add(new BankSampah("Depo Sampah Sorosutan", "Sorosutan", new LatLng(-7.8200, 110.3800)));
        bankSampahList.add(new BankSampah("Bank Sampah Igakanas", "Kabupaten Bantul, DIY", new LatLng(-7.8900, 110.3500)));

        // Setup Adapter
        adapter = new BankSampahAdapter(bankSampahList, new BankSampahAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BankSampah bankSampah) {
                if (mMap != null) {
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(bankSampah.getLokasi(), 16));
                    mMap.addMarker(new MarkerOptions()
                            .position(bankSampah.getLokasi())
                            .title(bankSampah.getNama())
                            .snippet(bankSampah.getAlamat()))
                            .showInfoWindow();
                }
            }
        });
        recyclerView.setAdapter(adapter);

        // Inisialisasi Map
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        // Mulai hitung jarak dan urutkan
        hitungJarakDanUrutkan();
    }

    private void hitungJarakDanUrutkan() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            // Minta izin
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }

        CancellationTokenSource cancellationTokenSource = new CancellationTokenSource();
        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, cancellationTokenSource.getToken())
            .addOnSuccessListener(requireActivity(), location -> {
                if (location != null) {
                    double userLat = location.getLatitude();
                    double userLng = location.getLongitude();

                    // Hitung jarak
                    for (BankSampah bank : bankSampahList) {
                        float[] results = new float[1];
                        Location.distanceBetween(userLat, userLng, bank.getLokasi().latitude, bank.getLokasi().longitude, results);
                        bank.setJarak(results[0]);
                    }

                    // Urutkan
                    Collections.sort(bankSampahList, new Comparator<BankSampah>() {
                        @Override
                        public int compare(BankSampah o1, BankSampah o2) {
                            return Float.compare(o1.getJarak(), o2.getJarak());
                        }
                    });

                    adapter.notifyDataSetChanged();

                    if (mMap != null) {
                        try {
                           mMap.setMyLocationEnabled(true);
                        } catch (SecurityException e) {
                           e.printStackTrace();
                        }
                    }
                }
            });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                hitungJarakDanUrutkan();
            }
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        // Koordinat Pusat Kota Yogyakarta
        LatLng yogyakarta = new LatLng(-7.7956, 110.3695);

        // Tambahkan marker
        for (BankSampah bank : bankSampahList) {
            mMap.addMarker(new MarkerOptions()
                    .position(bank.getLokasi())
                    .title(bank.getNama())
                    .snippet(bank.getAlamat()));
        }

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(yogyakarta, 12));
        
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        }
    }
}
