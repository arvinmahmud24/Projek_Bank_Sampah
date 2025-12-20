package com.example.myapplication

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*

class CariBankSampahActivity : AppCompatActivity(), OnMapReadyCallback {

    private var mMap: GoogleMap? = null
    private val bankSampahList = mutableListOf<BankSampah>()
    private val sampahList = mutableListOf<Sampah>()
    private lateinit var trashAdapter: SampahAdapter
    
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val markerMap = HashMap<String, Marker>()
    
    private var currentGrandTotal: Double = 0.0
    private var selectedBankName: String? = null
    private var userId: String? = null

    private lateinit var tvSelectedBank: TextView
    private lateinit var rvInput: RecyclerView
    private lateinit var btnSetorkan: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cari_bank_sampah)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        
        // Ambil sesi user
        val sharedPref = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        userId = sharedPref.getString("USER_ID", null)

        tvSelectedBank = findViewById(R.id.tvSelectedBank)
        rvInput = findViewById(R.id.recyclerViewInputSampah)
        btnSetorkan = findViewById(R.id.btn_setorkan_langsung)

        setupTrashRecyclerView()
        populateBankSampahList()
        populateTrashList()

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        btnSetorkan.setOnClickListener {
            if (currentGrandTotal > 0 && selectedBankName != null) {
                showConfirmationDialog()
            }
        }
    }

    private fun setupTrashRecyclerView() {
        rvInput.layoutManager = LinearLayoutManager(this)
        rvInput.visibility = View.GONE
        
        trashAdapter = SampahAdapter(sampahList, isReadOnly = false) { total ->
            currentGrandTotal = total
            if (total > 0) {
                btnSetorkan.visibility = View.VISIBLE
                btnSetorkan.text = "Setorkan (pts ${String.format("%,.0f", total)})"
            } else {
                btnSetorkan.visibility = View.GONE
            }
        }
        rvInput.adapter = trashAdapter
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        setupMapListeners()
        checkAndGetLocation()
        setupMapWithInitialData()
    }

    private fun checkAndGetLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
        } else {
            mMap?.isMyLocationEnabled = true
            hitungJarakAsli()
        }
    }

    private fun setupMapWithInitialData() {
        mMap?.clear()
        markerMap.clear()
        val boundsBuilder = LatLngBounds.Builder()

        for (bank in bankSampahList) {
            val marker = mMap?.addMarker(
                MarkerOptions()
                    .position(bank.lokasi)
                    .title(bank.nama)
                    .snippet("Klik untuk pilih")
            )
            marker?.tag = bank
            marker?.let { markerMap[bank.nama] = it }
            boundsBuilder.include(bank.lokasi)
        }

        if (bankSampahList.isNotEmpty()) {
            val bounds = boundsBuilder.build()
            mMap?.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100))
        }
    }

    private fun hitungJarakAsli() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) return

        val cancellationTokenSource = CancellationTokenSource()
        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, cancellationTokenSource.token)
            .addOnSuccessListener { location ->
                if (location != null) {
                    bankSampahList.forEach {
                        val results = FloatArray(1)
                        android.location.Location.distanceBetween(
                            location.latitude, location.longitude,
                            it.lokasi.latitude, it.lokasi.longitude, results
                        )
                        it.jarak = results[0]
                    }
                    bankSampahList.sortBy { it.jarak }
                }
            }
    }

    private fun setupMapListeners() {
        mMap?.setOnMarkerClickListener { marker ->
            val bank = marker.tag as? BankSampah
            if (bank != null) {
                selectedBankName = bank.nama
                tvSelectedBank.text = "Bank: ${bank.nama}"
                rvInput.visibility = View.VISIBLE
                mMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.position, 15f))
                marker.showInfoWindow()
            }
            true
        }
    }

    private fun showConfirmationDialog() {
        AlertDialog.Builder(this)
            .setTitle("Konfirmasi Setoran")
            .setMessage("Setor ke $selectedBankName senilai ${currentGrandTotal.toInt()} poin?")
            .setPositiveButton("Ya") { _, _ -> simpanKeFirebase() }
            .setNegativeButton("Tidak", null)
            .show()
    }

    private fun simpanKeFirebase() {
        if (userId == null) return
        val database = FirebaseDatabase.getInstance().reference
        
        val userRef = database.child("users").child(userId!!).child("poin")
        userRef.runTransaction(object : Transaction.Handler {
            override fun doTransaction(mutableData: MutableData): Transaction.Result {
                val current = mutableData.getValue(Int::class.java) ?: 0
                mutableData.value = current + currentGrandTotal.toInt()
                return Transaction.success(mutableData)
            }

            override fun onComplete(error: DatabaseError?, committed: Boolean, snapshot: DataSnapshot?) {
                if (committed) {
                    catatRiwayat()
                    Toast.makeText(this@CariBankSampahActivity, "Transaksi Berhasil!", Toast.LENGTH_SHORT).show()
                    rvInput.visibility = View.GONE
                    btnSetorkan.visibility = View.GONE
                    tvSelectedBank.text = "Pilih Bank di Peta"
                }
            }
        })
    }

    private fun catatRiwayat() {
        val riwayatRef = FirebaseDatabase.getInstance().getReference("riwayat_transaksi").push()
        val tanggal = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()).format(Date())
        val setoranData = trashAdapter.getSetoranData()
        val descBuilder = StringBuilder("Setor di $selectedBankName: ")
        setoranData.forEach { (pos, berat) -> if (berat > 0) descBuilder.append("${sampahList[pos].nama} ($berat Kg), ") }

        val data = hashMapOf(
            "userId" to userId,
            "tanggal" to tanggal,
            "deskripsi" to descBuilder.toString().removeSuffix(", "),
            "poin" to "+${currentGrandTotal.toInt()}",
            "isMasuk" to true
        )
        riwayatRef.setValue(data)
    }

    private fun populateBankSampahList() {
        bankSampahList.add(BankSampah("Bank Sampah Surolaras", "Jl. Suronatan No.Blok NG-2/51, Ngampilan", LatLng(-7.8005, 110.3610)))
        bankSampahList.add(BankSampah("Bank Sampah Mondoroko RW 7", "Jl. Mondorakan No.27, Kotagede", LatLng(-7.8286, 110.3957)))
        bankSampahList.add(BankSampah("Bank Sampah Suryo Resik", "Mj 2/822, RT.44/RW.13, Suryodiningratan", LatLng(-7.8180, 110.3650)))
        bankSampahList.add(BankSampah("Bank Sampah Tresno Tuhutentrem", "Jl. Sorosutan No.26, Umbulharjo", LatLng(-7.8240, 110.3750)))
        bankSampahList.add(BankSampah("Bank Sampah Sunten", "Kabupaten Bantul, DIY", LatLng(-7.8350, 110.4100)))
        bankSampahList.add(BankSampah("Bank Sampah Lestari RW.14", "Gg. Wiro Kresno, Tegalrejo", LatLng(-7.7800, 110.3550)))
        bankSampahList.add(BankSampah("Bank Sampah Induk Jogja", "Jl. Kemasan No.22, Kotagede", LatLng(-7.8290, 110.4000)))
        bankSampahList.add(BankSampah("Bank Sampah Mandiri", "Sewon, Bantul", LatLng(-7.8500, 110.3600)))
        bankSampahList.add(BankSampah("Bank Sampah Gowok", "Caturtunggal, Sleman", LatLng(-7.7830, 110.3950)))
        bankSampahList.add(BankSampah("Bank sampah simul 5", "Jl. Sidomulyo No.345", LatLng(-7.7900, 110.3500)))
        bankSampahList.add(BankSampah("Bank Sampah Bedeng Berseri", "Jl. Bumijo Kulon No.I, Jetis", LatLng(-7.7850, 110.3620)))
        bankSampahList.add(BankSampah("Bank Sampah Kusuma Pertiwi", "Jl. Ibu Ruswo No.35, Prawirodirjan", LatLng(-7.8040, 110.3680)))
        bankSampahList.add(BankSampah("BSM Pandeyan", "UH 5 No.873 A, Pandeyan", LatLng(-7.8150, 110.3850)))
        bankSampahList.add(BankSampah("Bank Sampah Blazent", "Jl. Taman Siswa No.7, Mergangsan", LatLng(-7.8080, 110.3780)))
        bankSampahList.add(BankSampah("Bank Sampah Gemah Ripah Bantul", "Kabupaten Bantul, DIY", LatLng(-7.9250, 110.3350)))
        bankSampahList.add(BankSampah("Bank Sampah Resik lan Pikoleh", "Unnamed Road", LatLng(-7.8800, 110.3400)))
        bankSampahList.add(BankSampah("BANK SAMPAH BERLIAN 07", "Jl. Tompeyan TR III", LatLng(-7.7850, 110.3550)))
        bankSampahList.add(BankSampah("Depo Sampah Sorosutan", "Sorosutan", LatLng(-7.8200, 110.3800)))
        bankSampahList.add(BankSampah("Bank Sampah Igakanas", "Kabupaten Bantul, DIY", LatLng(-7.8900, 110.3500)))
    }

    private fun populateTrashList() {
        sampahList.add(Sampah("Minyak Jelantah", "Rp. 3.600"))
        sampahList.add(Sampah("Ember Warna", "Rp. 1.500"))
        sampahList.add(Sampah("Besi A", "Rp. 2.760"))
        sampahList.add(Sampah("Kardus", "Rp. 1.260"))
        sampahList.add(Sampah("Tembaga", "Rp. 45.000"))
        sampahList.add(Sampah("Aki Bekas", "Rp. 6.000"))
        sampahList.add(Sampah("Kantong Kresek", "Rp. 50"))
        sampahList.add(Sampah("Beling Putih", "Rp. 120"))
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
