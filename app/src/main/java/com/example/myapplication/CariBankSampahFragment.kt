package com.example.myapplication

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
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

class CariBankSampahFragment : Fragment(), OnMapReadyCallback {

    private var mMap: GoogleMap? = null
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: BankSampahAdapter
    private val bankSampahList = mutableListOf<BankSampah>()
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val markerMap = HashMap<String, Marker>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_cari_bank_sampah, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        recyclerView = view.findViewById(R.id.recyclerViewBankSampah)
        recyclerView.layoutManager = LinearLayoutManager(context)

        populateBankSampahList()

        adapter = BankSampahAdapter(bankSampahList) { bankSampah ->
            // Pergi ke KatalogHargaActivity dengan membawa nama bank sampah
            val intent = Intent(requireContext(), KatalogHargaActivity::class.java).apply {
                putExtra("NAMA_BANK_SAMPAH", bankSampah.nama)
            }
            startActivity(intent)
        }
        recyclerView.adapter = adapter
        
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
        mapFragment?.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        setupMapListeners()
        checkAndGetLocation()
    }

    private fun checkAndGetLocation() {
        if (context?.let { ActivityCompat.checkSelfPermission(it, Manifest.permission.ACCESS_FINE_LOCATION) } != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
        } else {
            setupMapWithInitialData()
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
                    .snippet("Klik untuk rute")
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
        if (context?.let { ActivityCompat.checkSelfPermission(it, Manifest.permission.ACCESS_FINE_LOCATION) } != PackageManager.PERMISSION_GRANTED) return

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
                    adapter.notifyDataSetChanged()
                }
            }
    }

    private fun setupMapListeners() {
        mMap?.setOnMarkerClickListener { marker ->
            mMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.position, 17f), 1500, null)
            marker.showInfoWindow()
            true
        }

        mMap?.setOnInfoWindowClickListener { marker ->
            val bank = marker.tag as? BankSampah
            bank?.let {
                val gmmIntentUri = Uri.parse("google.navigation:q=${it.lokasi.latitude},${it.lokasi.longitude}")
                val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri).apply {
                    setPackage("com.google.android.apps.maps")
                }
                if (mapIntent.resolveActivity(requireActivity().packageManager) != null) {
                    startActivity(mapIntent)
                } else {
                    Toast.makeText(context, "Google Maps tidak terinstal.", Toast.LENGTH_SHORT).show()
                }
            }
        }
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

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            checkAndGetLocation()
        }
    }
}