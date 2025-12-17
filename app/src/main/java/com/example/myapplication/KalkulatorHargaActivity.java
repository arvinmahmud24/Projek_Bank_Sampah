package com.example.myapplication;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.NumberFormat;
import java.util.Locale;

public class KalkulatorHargaActivity extends AppCompatActivity {

    private TextView tvNamaSampah, tvHargaPerUnit, tvTotalHarga;
    private EditText etBerat;
    private Button btnHitung;
    private int hargaSatuan = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kalkulator_harga);

        tvNamaSampah = findViewById(R.id.tvNamaSampah);
        tvHargaPerUnit = findViewById(R.id.tvHargaPerUnit);
        tvTotalHarga = findViewById(R.id.tvTotalHarga);
        etBerat = findViewById(R.id.etBerat);
        btnHitung = findViewById(R.id.btnHitung);

        // Ambil data dari Intent
        String namaSampah = getIntent().getStringExtra("NAMA_SAMPAH");
        String hargaSampahString = getIntent().getStringExtra("HARGA_SAMPAH"); // e.g., "Rp. 5,000 / Liter"

        // Parse harga dari string (menghilangkan non-digit)
        if (hargaSampahString != null) {
            String hargaAngka = hargaSampahString.replaceAll("[^0-9]", "");
            try {
                hargaSatuan = Integer.parseInt(hargaAngka);
            } catch (NumberFormatException e) {
                hargaSatuan = 0;
            }
        }

        // Tampilkan info
        tvNamaSampah.setText(namaSampah);
        tvHargaPerUnit.setText(hargaSampahString);

        // Listener untuk tombol hitung
        btnHitung.setOnClickListener(v -> hitungTotal());

    }

    private void hitungTotal() {
        String beratString = etBerat.getText().toString();
        if (beratString.isEmpty()) {
            tvTotalHarga.setText("Rp. 0");
            return;
        }

        try {
            double berat = Double.parseDouble(beratString);
            double total = berat * hargaSatuan;

            // Format ke Rupiah
            NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
            tvTotalHarga.setText(formatRupiah.format(total));

        } catch (NumberFormatException e) {
            tvTotalHarga.setText("Input tidak valid");
        }
    }
}
