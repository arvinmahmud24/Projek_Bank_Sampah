package com.example.myapplication

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.myapplication.databinding.ActivityMainBinding
import com.example.myapplication.ui.home.HomeFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var userId: String? = null
    private var username: String? = null
    private var email: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userId = intent.getStringExtra("USER_ID")
        username = intent.getStringExtra("USERNAME")
        email = intent.getStringExtra("EMAIL")

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            var selectedFragment: Fragment? = null
            when (item.itemId) {
                R.id.nav_home -> {
                    selectedFragment = HomeFragment().apply {
                        arguments = Bundle().apply {
                            putString("USER_ID", userId)
                            putString("USERNAME", username)
                            putString("EMAIL", email)
                        }
                    }
                }
                R.id.nav_transaction -> {
                    selectedFragment = TransaksiFragment()
                }
                R.id.nav_profile -> {
                    selectedFragment = ProfilFragment().apply {
                        arguments = Bundle().apply {
                            putString("USER_ID", userId)
                            putString("USERNAME", username)
                            putString("EMAIL", email)
                        }
                    }
                }
            }
            if (selectedFragment != null) {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, selectedFragment).commit()
            }
            true
        }

        // Set default fragment
        if (savedInstanceState == null) {
            binding.bottomNavigation.selectedItemId = R.id.nav_home
        }
    }

    // Method pembantu untuk pindah tab dari fragment lain
    fun setSelectedTab(itemId: Int) {
        binding.bottomNavigation.selectedItemId = itemId
    }
}
