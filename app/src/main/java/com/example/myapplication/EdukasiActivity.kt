package com.example.myapplication

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.databinding.ActivityEdukasiBinding

class EdukasiActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEdukasiBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEdukasiBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}
