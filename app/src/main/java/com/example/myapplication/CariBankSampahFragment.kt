package com.example.myapplication

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
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
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.firebase.database.*

class CariBankSampahFragment : Fragment(), OnMapReadyCallback {

    private var mMap: GoogleMap? = null
    private lateinit var recyclerView: RecyclerView
    private lateinit var bankSampahAdapter: BankSampahAdapter
    private val bankSampahList = mutableListOf<BankSampah>()
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var databaseReference: DatabaseReference
    private lateinit var bankSampahListener: ValueEventListener

    private val markerMap = HashMap<String, Marker>()

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            hitungJarakAsli()
        } else {
            Toast.makeText(context, "Izin lokasi ditolak.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_cari_bank_sampah, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        recyclerView = view.findViewById(R.id.recyclerViewBankSampah)
        recyclerView.layoutManager = LinearLayoutManager(context)
        bankSampahAdapter = BankSampahAdapter(bankSampahList) { bankSampah ->
            val intent = Intent(activity, KatalogHargaActivity::class.java).apply {
                putExtra("BANK_SAMPAH_ID", bankSampah.id)
            }
            startActivity(intent)
        }
        recyclerView.adapter = bankSampahAdapter

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
        mapFragment?.getMapAsync(this)

        fetchBankSampahData()
    }

    private fun fetchBankSampahData() {
        databaseReference = FirebaseDatabase.getInstance().getReference("bank_sampah")
        bankSampahListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (context == null) return

                if (!dataSnapshot.exists()) {
                    Log.w(TAG, "Snapshot tidak ada! Path: ${databaseReference}")
                    Toast.makeText(context, "Data bank sampah tidak ditemukan.", Toast.LENGTH_LONG).show()
                    return
                }

                bankSampahList.clear()
                for (snapshot in dataSnapshot.children) {
                    val bankSampah = snapshot.getValue(BankSampah::class.java)
                    if (bankSampah != null) {
                        bankSampah.id = snapshot.key ?: ""
                        bankSampahList.add(bankSampah)
                    } else {
                        Log.e(TAG, "Gagal parsing data. Key: ${snapshot.key}")
                    }
                }
                bankSampahAdapter.notifyDataSetChanged()
                hitungJarakAsli()
                updateMapMarkers()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e(TAG, "Firebase onCancelled: ${databaseError.message}")
                Toast.makeText(context, "Gagal memuat data: ${databaseError.message}", Toast.LENGTH_LONG).show()
            }
        }
        databaseReference.addValueEventListener(bankSampahListener)
    }

    private fun hitungJarakAsli() {
        if (context == null || ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            return
        }

        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, CancellationTokenSource().token)
            .addOnSuccessListener { location: Location? ->
                if (location != null && context != null) {
                    bankSampahList.forEach { bank ->
                        val results = FloatArray(1)
                        Location.distanceBetween(
                            location.latitude, location.longitude,
                            bank.latitude, bank.longitude, results
                        )
                        bank.jarak = results[0]
                    }
                    bankSampahList.sortBy { it.jarak }
                    bankSampahAdapter.notifyDataSetChanged()
                }
            }
    }

    private fun updateMapMarkers() {
        mMap?.let { map ->
            map.clear()
            markerMap.clear()

            if (bankSampahList.isEmpty()) return

            val builder = LatLngBounds.Builder()
            bankSampahList.forEach { bank ->
                val lokasi = bank.getLokasi()
                val marker = map.addMarker(
                    MarkerOptions()
                        .position(lokasi)
                        .title(bank.nama)
                        .snippet("Klik untuk lihat katalog harga")
                )
                marker?.tag = bank
                marker?.let { markerMap[bank.id] = it }
                builder.include(lokasi)
            }
            map.moveCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 150))
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        if (context != null && ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap?.isMyLocationEnabled = true
        }

        mMap?.setOnInfoWindowClickListener { marker ->
            val bank = marker.tag as? BankSampah
            bank?.let {
                val intent = Intent(activity, KatalogHargaActivity::class.java).apply {
                    putExtra("BANK_SAMPAH_ID", it.id)
                }
                startActivity(intent)
            }
        }

        mMap?.setOnMarkerClickListener { marker ->
            marker.showInfoWindow()
            mMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.position, 15f))
            true
        }

        updateMapMarkers()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (::databaseReference.isInitialized && ::bankSampahListener.isInitialized) {
            databaseReference.removeEventListener(bankSampahListener)
        }
        mMap = null
    }

    companion object {
        private const val TAG = "CariBankSampahFragment"
    }
}
