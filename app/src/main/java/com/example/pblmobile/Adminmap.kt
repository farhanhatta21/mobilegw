package com.example.pblmobile

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.pblmobile.Api.RetrofitClient
import com.example.pblmobile.Api.ApiService
import com.example.pblmobile.Models.TempatsampahResponse
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Adminmap : Fragment(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var searchView: SearchView
    private var favoriteMarker: Marker? = null

    // API call response data
    private val apiService: ApiService = RetrofitClient.instance
    private lateinit var refreshJob: Job

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.admin_map, container, false)

        // Initialize views
        searchView = view.findViewById(R.id.search_view)

        // Check location permission
        checkLocationPermission()

        // Initialize Google Maps API
        val mapFragment = childFragmentManager.findFragmentById(R.id.map_fragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

        setupSearchViewListener()

        return view
    }

    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1001
            )
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Enable MyLocation layer if permissions are granted
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true
        }

        // Fetch trash data from API
        fetchTempatsampahData()

        // Move camera to a default location (for example)
        val defaultLocation = LatLng(-5.142577, 119.522702)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 15f))

        // Start background job to refresh data every 30 seconds
        startRefreshingData()
    }

    private fun fetchTempatsampahData() {
        apiService.getTempatsampahData().enqueue(object : Callback<TempatsampahResponse> {
            override fun onResponse(
                call: Call<TempatsampahResponse>,
                response: Response<TempatsampahResponse>
            ) {
                if (response.isSuccessful) {
                    val body = response.body()

                    // Debug log: print the full response body to inspect
                    Log.d("Adminmap", "Full response body: $body")

                    if (body != null && body.status) {
                        val tempatSampahList = body.tempat_sampah

                        // Clear the existing markers before adding new ones
                        mMap.clear()

                        // Build a LatLngBounds to adjust the camera and fit all markers
                        val boundsBuilder = LatLngBounds.builder()

                        // Loop through the trash data and add markers to the map
                        tempatSampahList.forEach { tempatSampah ->
                            val lat = tempatSampah.longtitude.toDouble() // Longitude first
                            val lng = tempatSampah.latitude.toDouble()  // Latitude second
                            val status = tempatSampah.status

                            // Determine the marker icon based on the trash status
                            val markerIcon = when (status) {
                                "kosong" -> R.drawable.trashmarkz_green
                                "terisi" -> R.drawable.trashmarkz_yellow
                                else -> R.drawable.trashmarkz_red
                            }

                            val bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(
                                Bitmap.createScaledBitmap(
                                    BitmapFactory.decodeResource(resources, markerIcon),
                                    100, 100, true
                                )
                            )

                            // Add marker to the map with title and status as snippet
                            val markerOptions = MarkerOptions()
                                .position(LatLng(lat, lng))
                                .title("Tempat Sampah ${tempatSampah.id_tempatsampah}")
                                .snippet("Status: $status") // Add status here
                                .icon(bitmapDescriptor)

                            // Add marker to the map
                            mMap.addMarker(markerOptions)

                            // Include the marker position in the bounds for camera adjustment
                            boundsBuilder.include(LatLng(lat, lng))
                        }

                        // Adjust the camera to fit all markers
                        val bounds = boundsBuilder.build()
                        val cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 100)
                        mMap.moveCamera(cameraUpdate)

                        Toast.makeText(requireContext(), "map telah diperbarui", Toast.LENGTH_SHORT).show()
                    } else {
                        // If body is null or status is false
                        Log.e("Adminmap", "API response is not successful or status is false")
                        Toast.makeText(requireContext(), "Data tempat sampah tidak ditemukan", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    // Handle unsuccessful response
                    Log.e("Adminmap", "API request failed with status: ${response.code()} - ${response.message()}")
                    Toast.makeText(requireContext(), "Gagal memuat data tempat sampah", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<TempatsampahResponse>, t: Throwable) {
                // Log the error message
                Log.e("Adminmap", "API Call failed: ${t.message}")
                Toast.makeText(requireContext(), "Gagal mengambil data tempat sampah: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setupSearchViewListener() {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (!query.isNullOrEmpty()) performSearch(query)
                return true
            }

            override fun onQueryTextChange(query: String?): Boolean = true
        })
    }

    private fun performSearch(query: String) {
        val request = com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest.builder()
            .setQuery(query)
            .build()

        val placesClient = com.google.android.libraries.places.api.Places.createClient(requireContext())
        placesClient.findAutocompletePredictions(request)
            .addOnSuccessListener { response ->
                val searchResults = response.autocompletePredictions
                if (searchResults.isNotEmpty()) fetchPlaceDetails(searchResults[0].placeId)
                else Toast.makeText(requireContext(), "No results found", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Search failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun fetchPlaceDetails(placeId: String) {
        val placesClient = com.google.android.libraries.places.api.Places.createClient(requireContext())
        val request = com.google.android.libraries.places.api.net.FetchPlaceRequest.builder(
            placeId,
            listOf(
                com.google.android.libraries.places.api.model.Place.Field.LAT_LNG,
                com.google.android.libraries.places.api.model.Place.Field.NAME,
                com.google.android.libraries.places.api.model.Place.Field.ADDRESS
            )
        ).build()

        placesClient.fetchPlace(request)
            .addOnSuccessListener { response ->
                val latLng = response.place.latLng
                if (latLng != null) {
                    mMap.clear()
                    mMap.addMarker(MarkerOptions().position(latLng).title(response.place.name))
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Unable to find location: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun startRefreshingData() {
        // Start refreshing data every 30 seconds
        refreshJob = CoroutineScope(Dispatchers.Main).launch {
            while (isActive) {
                fetchTempatsampahData()
                delay(10000) // Refresh every 30 seconds
            }
        }
    }

    override fun onStop() {
        super.onStop()
        // Cancel the refreshing job when the fragment is stopped
        refreshJob.cancel()
    }

}
