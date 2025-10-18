package com.example.myapplication;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main); // hubungkan ke layout XML kamu

    // Ambil referensi dari XML
    BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

    // Listener untuk navigasi bawah
    bottomNavigationView.setOnItemSelectedListener(item -> {
        int itemId = item.getItemId(); // Ambil ID item

        if (itemId == R.id.nav_home) {
            Toast.makeText(this, "Home dipilih", Toast.LENGTH_SHORT).show();
            return true;
        } else if (itemId == R.id.nav_map) {
            Toast.makeText(this, "Peta dipilih", Toast.LENGTH_SHORT).show();
            return true;
        } else if (itemId == R.id.nav_transaction) {
            Toast.makeText(this, "Transaksi dipilih", Toast.LENGTH_SHORT).show();
            return true;
        } else if (itemId == R.id.nav_profile) {
            Toast.makeText(this, "Profil dipilih", Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    });
}
}
