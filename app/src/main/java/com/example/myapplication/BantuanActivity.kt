package com.example.myapplication

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

class BantuanActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bantuan)

        val toolbar: Toolbar = findViewById(R.id.toolbarBantuan)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val btnWA: Button = findViewById(R.id.btnHubungiWA)
        val btnEmail: Button = findViewById(R.id.btnHubungiEmail)

        btnWA.setOnClickListener {
            val phoneNumber = "628123456789" // Contoh nomor WA
            val url = "https://api.whatsapp.com/send?phone=$phoneNumber"
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            try {
                startActivity(intent)
            } catch (e: Exception) {
                Toast.makeText(this, "WhatsApp tidak terinstal", Toast.LENGTH_SHORT).show()
            }
        }

        btnEmail.setOnClickListener {
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:support@banksampah.com")
                putExtra(Intent.EXTRA_SUBJECT, "Bantuan Aplikasi Bank Sampah")
            }
            startActivity(Intent.createChooser(intent, "Kirim Email..."))
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}
